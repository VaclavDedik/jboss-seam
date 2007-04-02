package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;

import java.util.List;
import java.io.Serializable;

@Name("nodePermissions")
@Scope(ScopeType.CONVERSATION)
public class NodePermissions implements Serializable {

    @In
    Node currentNode;

    private Role.AccessLevel writeAccessLevel;
    private Role.AccessLevel readAccessLevel;

    @Create
    public void setCurrentNodePermissions() {

        // Set permission defaults
        List<Role.AccessLevel> accessLevelsList =
                (List<Role.AccessLevel>) Component.getInstance("accessLevelsList");

        writeAccessLevel = accessLevelsList.get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(currentNode.getWriteAccessLevel())
            )
        );
        readAccessLevel = accessLevelsList.get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(currentNode.getReadAccessLevel())
            )
        );
    }

    public Role.AccessLevel getWriteAccessLevel() {
        return writeAccessLevel;
    }

    public void setWriteAccessLevel(Role.AccessLevel writeAccessLevel) {
        this.writeAccessLevel = writeAccessLevel;
        currentNode.setWriteAccessLevel(
            writeAccessLevel != null ? writeAccessLevel.getAccessLevel() : UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL
        );
    }

    public Role.AccessLevel getReadAccessLevel() {
        return readAccessLevel;
    }

    public void setReadAccessLevel(Role.AccessLevel readAccessLevel) {
        this.readAccessLevel = readAccessLevel;
        currentNode.setReadAccessLevel(
            readAccessLevel != null ? readAccessLevel.getAccessLevel() : UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL
        );
    }

}
