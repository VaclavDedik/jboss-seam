package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.ui.WikiUtil;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator {

    @In
    private UserDAO userDAO;

    @In
    private NodeDAO nodeDAO;

    @In
    private Hash hashUtil;

    @In
    private Identity identity;

    @RequestParameter
    private String activationCode;

    @Transactional
    public boolean authenticate() {

        if (org.jboss.seam.wiki.core.dao.UserRoleAccessFactory.GUEST_USERNAME.equals(identity.getUsername())) return false;

        User user = userDAO.findUser(identity.getUsername(), true, true);
        if (user == null ||
            identity.getPassword() == null ||
            !user.getPasswordHash().equalsIgnoreCase(hashUtil.hash(identity.getPassword())))
            return false;

        // We don't use Seams Role class, wiki currently only uses numeric access levels
        Role bestRole = (Role)Component.getInstance("guestRole");
        for (Role role : user.getRoles()) {
            if (role.getAccessLevel() > bestRole.getAccessLevel()) bestRole = role;
        }

        // Outject current user and access level
        Contexts.getSessionContext().set("currentUser", user);
        Contexts.getSessionContext().set("currentAccessLevel", bestRole.getAccessLevel());

        return true;
    }

    @Transactional
    public String activate() {
        User user = userDAO.findUserWithActivationCode(activationCode);
        if (user != null) {
            user.setActivated(true);
            user.setActivationCode(null);

            // Create home directory
            Directory memberArea = (Directory)Component.getInstance("memberArea");

            Directory homeDirectory = new Directory(user.getUsername());
            homeDirectory.setWikiname(WikiUtil.convertToWikiName(homeDirectory.getName()));
            homeDirectory.setAreaNumber(memberArea.getAreaNumber());
            homeDirectory.setCreatedBy(user);
            homeDirectory.setWriteAccessLevel(UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL);
            homeDirectory.setReadAccessLevel(UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL);
            memberArea.addChild(homeDirectory);
            user.setMemberHome(homeDirectory);
            nodeDAO.makePersistent(homeDirectory);

            // Create home page
            Document homePage = new Document("Home of " + user.getUsername());
            homePage.setWikiname(WikiUtil.convertToWikiName(homePage.getName()));
            homePage.setCreatedBy(user);
            homePage.setAreaNumber(homeDirectory.getAreaNumber());
            homePage.setContent("This is the homepage of " + user.getFirstname() + " " + user.getLastname() + ".");
            homePage.setWriteAccessLevel(UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL);
            homePage.setReadAccessLevel(UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL);
            homeDirectory.addChild(homePage);
            homeDirectory.setDefaultDocument(homePage);
            nodeDAO.makePersistent(homeDirectory);

            Contexts.getEventContext().set("activatedUser", user);

            return "activated";
        } else {
            return "notFound";
        }
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
        return ((Role)Component.getInstance("guestRole")).getAccessLevel();
    }

}
