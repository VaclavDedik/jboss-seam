package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.ScopeType;

@Name("nodePermissions")
@Scope(ScopeType.CONVERSATION)
public class NodePermissions {

    @In
    Node currentNode;

    private org.jboss.seam.wiki.core.model.Role writableByRole;
    private org.jboss.seam.wiki.core.model.Role readableByRole;

    @In
    private UserDAO userDAO;

    @Create
    public void setCurrentNodePermissions() {
        // Set permission defaults
        writableByRole = userDAO.findRole(currentNode.getWriteAccessLevel());
        readableByRole = userDAO.findRole(currentNode.getReadAccessLevel());
    }

    public org.jboss.seam.wiki.core.model.Role getWritableByRole() {
        return writableByRole;
    }

    public void setWritableByRole(org.jboss.seam.wiki.core.model.Role writableByRole) {
        this.writableByRole = writableByRole;
        currentNode.setWriteAccessLevel(writableByRole != null ? writableByRole.getAccessLevel() : 1000);
    }

    public org.jboss.seam.wiki.core.model.Role getReadableByRole() {
        return readableByRole;
    }

    public void setReadableByRole(org.jboss.seam.wiki.core.model.Role readableByRole) {
        this.readableByRole = readableByRole;
        currentNode.setReadAccessLevel(readableByRole != null ? readableByRole.getAccessLevel() : 1000);
    }


}
