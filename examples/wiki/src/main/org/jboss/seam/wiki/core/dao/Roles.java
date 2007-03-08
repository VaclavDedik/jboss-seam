package org.jboss.seam.wiki.core.dao;

import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.annotations.*;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Roles implements Serializable {

    @Name("roles")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class RoleList {

        @In
        protected EntityManager entityManager;

        protected List<Role> roles;

        @Unwrap
        @SuppressWarnings({"unchecked"})
        @Transactional
        public List<Role> getRoles() {
            if (roles == null) {
                entityManager.joinTransaction();
                roles = (List<Role>)entityManager
                            .createQuery("select r from Role r order by r.accessLevel desc")
                            .getResultList();
                if (roles.size() == 0)
                    throw new RuntimeException("You need to INSERT at least one role with access level 1000 into the database");
                }
            return roles;
        }

    }

    @Name("roleMap")
    @Scope(ScopeType.CONVERSATION)
    @AutoCreate
    public static class RoleMap {

        @In
        List<Role> roles;

        Map<Integer,Role> roleMap;

        @Unwrap
        public Map<Integer,Role> getRoleMap() {
            if (roleMap == null) {
                roleMap = new HashMap<Integer, Role>(roles.size());
                for (Role role : roles) {
                    roleMap.put(role.getAccessLevel(), role);
                }
            }

            return roleMap;
        }
    }


}
