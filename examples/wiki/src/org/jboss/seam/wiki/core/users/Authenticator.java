package org.jboss.seam.wiki.core.users;

import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.util.Hash;
import org.jboss.seam.ScopeType;
import org.jboss.seam.security.Identity;

@Name("authenticator")
public class Authenticator {

    @In(create = true)
    private UserDAO userDAO;

    @In(create = true)
    private Hash hashUtil;

    @Out(required = false, scope = ScopeType.SESSION)
    private User authenticatedUser;

    @In
    private Identity identity;

    @RequestParameter
    private String activationCode;

    @Transactional
    public boolean authenticate() {
        User user = userDAO.findUser(identity.getUsername(), true);
        if (user == null ||
            identity.getPassword() == null ||
            !user.getPasswordHash().equalsIgnoreCase(hashUtil.hash(identity.getPassword())))
            return false;

        authenticatedUser = user;
        for (Role role : user.getRoles()) identity.addRole(role.getName());

        return true;
    }

    @Transactional
    public String activate() {
        User user = userDAO.findUserWithActivationCode(activationCode);
        if (user != null) {
            user.setActivated(true);
            user.setActivationCode(null);
            return "activated";
        } else {
            return "notFound";
        }
    }

}
