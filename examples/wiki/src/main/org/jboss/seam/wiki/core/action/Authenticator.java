/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.util.Base64;
import org.jboss.seam.wiki.core.action.prefs.UserManagementPreferences;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.engine.MacroWikiTextRenderer;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Name("authenticator")
public class Authenticator {

    @Logger
    Log log;

    @In
    private UserDAO userDAO;

    @In
    private Hash hashUtil;

    @In
    private Identity identity;

    @In("#{preferences.get('UserManagement')}")
    UserManagementPreferences prefs;

    private String activationCode;
    public String getActivationCode() { return activationCode; }
    public void setActivationCode(String activationCode) { this.activationCode = activationCode; }

    public boolean authenticateBasicHttp(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !!auth.toUpperCase().startsWith("Basic ")) {
            log.debug("Basic HTTP authorization header not found");
            return false;
        }
        String userpassEncoded = auth.substring(6);
        String userpassDecoded = new String(Base64.decode(userpassEncoded));
        if (!userpassDecoded.contains(":")) {
            log.debug("Basic HTTP authorization password not supplied");
            return false;
        }
        String username = userpassDecoded.substring(0, userpassDecoded.indexOf(":"));
        String password = userpassDecoded.substring(userpassDecoded.indexOf(":")+1);

        log.debug("Basic HTTP authentication for user: " + username);
        User user = getUserForCredentials(username, password);
        if (user == null) return false;
        setRolesAndAccessLevels(user);
        Events.instance().raiseEvent("User.loggedInBasicHttp", user);
        return true;
    }

    public boolean authenticate() {

        User user = getUserForCredentials(identity.getUsername(), identity.getPassword());
        if (user == null) return false;

        setRolesAndAccessLevels(user);

        // Set last login (storing the previous last login too, so we can create deltas between the two logins)
        user.setPreviousLastLoginOn(user.getLastLoginOn());
        user.setLastLoginOn(new Date());

        Events.instance().raiseEvent("User.loggedIn", user);
        return true;
    }

    private User getUserForCredentials(String username, String password) {
        if (User.GUEST_USERNAME.equals(username)) return null;
        User user = userDAO.findUser(username, true, true);
        if (user == null || password == null || !user.getPasswordHash().equalsIgnoreCase(hashUtil.hash(password))) {
            log.info("Invalid authentication credentials for user: " + username);
            return null;
        }
        log.debug("Successfully authenticated user: " + user.getUsername());
        return user;
    }

    private void setRolesAndAccessLevels(User user) {

        // We don't use Seams Role class, wiki currently only uses numeric access levels
        Role bestRole = (Role)Component.getInstance("guestRole");
        for (Role role : user.getRoles()) {
            if (role.getAccessLevel() > bestRole.getAccessLevel()) bestRole = role;
        }

        // Outject current user and access level
        Contexts.getSessionContext().set("currentUser", user);
        Contexts.getSessionContext().set("currentAccessLevel", bestRole.getAccessLevel());

    }

    public String activate() {
        User user = userDAO.findUserWithActivationCode(activationCode);
        if (user != null) {
            user.setActivated(true);
            user.setActivationCode(null);
            Contexts.getEventContext().set("activatedUser", user);

            // Optionally, create home directory
            if ( prefs.getCreateHomeAfterUserActivation() ) {
                createHomeDirectory(user);
            }

            return "activated";
        } else {
            return "notFound";
        }
    }

    public void createHomeDirectory(User user) {

        WikiNodeDAO nodeDAO = (WikiNodeDAO)Component.getInstance("wikiNodeDAO");

        // Create home directory
        WikiDirectory memberArea = (WikiDirectory)Component.getInstance("memberArea");

        WikiDirectory homeDirectory = new WikiDirectory(user.getUsername());
        homeDirectory.setWikiname(WikiUtil.convertToWikiName(homeDirectory.getName()));
        homeDirectory.setAreaNumber(memberArea.getAreaNumber());
        homeDirectory.setCreatedBy(user);
        homeDirectory.setWriteAccessLevel(Role.ADMINROLE_ACCESSLEVEL);
        homeDirectory.setReadAccessLevel(Role.GUESTROLE_ACCESSLEVEL);
        homeDirectory.setParent(memberArea);
        user.setMemberHome(homeDirectory);

        // Create feed for home directory
        Feed feed = new Feed();
        feed.setDirectory(homeDirectory);
        feed.setAuthor(homeDirectory.getCreatedBy().getFullname());
        feed.setTitle(homeDirectory.getName());
        homeDirectory.setFeed(feed);

        nodeDAO.makePersistent(homeDirectory);

        // Create home page
        WikiDocument homePage = new WikiDocument();
        homePage.setName("Home of " + user.getUsername());
        homePage.setWikiname(WikiUtil.convertToWikiName(homePage.getName()));
        homePage.setCreatedBy(user);
        homePage.setAreaNumber(homeDirectory.getAreaNumber());
        homePage.setContent(prefs.getHomepageDefaultContent());
        homePage.setWriteAccessLevel(Role.ADMINROLE_ACCESSLEVEL);
        homePage.setReadAccessLevel(Role.GUESTROLE_ACCESSLEVEL);

        MacroWikiTextRenderer renderer = MacroWikiTextRenderer.renderMacros(homePage.getContent());
        homePage.setContentMacros(renderer.getMacros());
        homePage.setContentMacrosString(renderer.getMacrosString());

        homePage.setParent(homeDirectory);
        homeDirectory.setDefaultFile(homePage);

        nodeDAO.makePersistent(homePage);
    }

    public String logout() {
        Identity.instance().logout();
        return "loggedOut";
    }

    /**
     * Assigns the Guest user to 'currentUser' when 'currentUser' is first referenced. If a
     * user actually logs in, the 'currentUser' is reset.
     * @return User Guest user
     */
    @Factory(value = "currentUser", scope = ScopeType.SESSION, autoCreate = true)
    public User getGuestUser() {
        return (User) Component.getInstance("guestUser");
    }

    /**
     * Assigns the context variable 'currentAccessLevel' when no user is logged in.
     * @return Integer Guest access level.
     */
    @Factory(value = "currentAccessLevel", scope = ScopeType.SESSION, autoCreate = true)
    public Integer getGuestAccessLevel() {
        return Role.GUESTROLE_ACCESSLEVEL;
    }

}
