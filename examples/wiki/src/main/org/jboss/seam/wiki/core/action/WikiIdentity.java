package org.jboss.seam.wiki.core.action;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.APPLICATION;

import org.jboss.seam.security.Identity;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.GlobalPreferences;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.Component;

/**
 * Need this until Drools fixes bugs and becomes usable/debuggable.
 *
 */
@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Intercept(NEVER)
@Install(precedence=APPLICATION)
@Startup
public class WikiIdentity extends Identity {

    private User currentUser;
    private Integer currentAccessLevel;
    private GlobalPreferences globalPrefs;

    public boolean hasPermission(String name, String action, Object... args) {

        currentUser = (User)Component.getInstance("currentUser");
        currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
        globalPrefs = (GlobalPreferences)Component.getInstance("globalPrefs");

        if (args == null || args.length == 0) {
            // All the security checks currently need arguments...
            return false;
        }

        if ("Node".equals(name) && "create".equals(action)) {
            return checkCreateAccess( (Directory)args[0]);
        } else
        if ("Node".equals(name) && "edit".equals(action)) {
            return checkEditAccess((Node)args[0]);
        } else
        if ("Node".equals(name) && "read".equals(action)) {
            return checkReadAccess((Node)args[0]);
        } else
        if ("Node".equals(name) && "changeAccessLevel".equals(action)) {
            return checkRaiseAccessLevel((Node)args[0]);
        } else
        if ("User".equals(name) && "edit".equals(action)) {
            return checkEditUser((User)args[0]);
        } else
        if ("User".equals(name) && "editRoles".equals(action)) {
            return checkEditUserRoles((User)args[0]);
        } else
        if ("Node".equals(name) && "editMenu".equals(action)) {
            return checkEditMenu((Node)args[0]);
        }

        return false;
    }

    /*
        User either needs to have the access level of the parent directory
        or the user is the creator of the parent directory
    */
    private boolean checkCreateAccess(Directory directory) {
        if (globalPrefs.getMemberAreaId().equals(directory.getId())) return false; // Member home dir is immutable
        if (directory.getWriteAccessLevel() == UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL) return true;
        int dirWriteAccessLevel = directory.getWriteAccessLevel();
        User dirCreator = directory.getCreatedBy();
        if (
            currentAccessLevel >= dirWriteAccessLevel
            ||
            currentUser.getId().equals(dirCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User either needs to have the access level of the edited node or has to be the creator
    */
    private boolean checkReadAccess(Node node) {
        if (node.getReadAccessLevel() == UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL) return true;
        int nodeReadAccessLevel = node.getReadAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (currentAccessLevel >= nodeReadAccessLevel
            ||
            currentUser.getId().equals(nodeCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User either needs to have the access level of the edited node or has to be the creator
    */
    private boolean checkEditAccess(Node node) {
        if (globalPrefs.getMemberAreaId().equals(node.getId())) return false; // Member home dir is immutable
        if (node.getWriteAccessLevel() == UserRoleAccessFactory.GUESTROLE_ACCESSLEVEL) return true;
        int nodeWriteAccessLevel = node.getWriteAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (currentAccessLevel >= nodeWriteAccessLevel
            ||
            currentUser.getId().equals(nodeCreator.getId())
           )
           return true;

        return false;
    }

    /*
        User can't persist or update a node and assign a higher access level than
        he has, unless he is the creator
    */
    private boolean checkRaiseAccessLevel(Node node) {
        if (globalPrefs.getMemberAreaId().equals(node.getId())) return false; // Member home dir is immutable
        int desiredWriteAccessLevel = node.getWriteAccessLevel();
        int desiredReadAccessLevel = node.getReadAccessLevel();
        User nodeCreator = node.getCreatedBy();

        if (
            ( desiredReadAccessLevel <= currentAccessLevel
              &&
              desiredWriteAccessLevel <= currentAccessLevel )
            ||
            ( nodeCreator == null
              ||
              currentUser.getId().equals(nodeCreator.getId()) )
           )
           return true;

        return false;
    }

    /*
        Only admins can change roles of a user
    */
    private boolean checkEditUserRoles(User currentUser) {
        return currentAccessLevel == UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL;
    }

    /*
        Only admins can edit users, or the user himself
    */
    private boolean checkEditUser(User user) {
        if (currentAccessLevel == UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL) return true;
        if (currentUser.getId() == user.getId()) return true;
        return false;
    }

    /*
        Only admins can edit the main menu
    */
    private boolean checkEditMenu(Node node) {
        if (currentAccessLevel == UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL) return true;
        return false;
    }

}
