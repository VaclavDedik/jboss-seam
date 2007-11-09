package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.EntityManager;
import java.io.Serializable;

@Name("forumListHome")
@Scope(ScopeType.PAGE)
public class ForumListHome implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    @In
    ForumHome forumHome;

    private boolean managed;

    public boolean isManaged() {
        return managed;
    }

    public void setManaged(boolean managed) {
        this.managed = managed;
    }

    public void manage() {
        managed = true;
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', currentDirectory)}")
    @RaiseEvent("Forum.forumListRefresh")
    public void moveNode(int currentPosition, int newPosition) {
        Directory forumDirectory = (Directory)Component.getInstance("currentDirectory");
        forumDirectory = restrictedEntityManager.find(Directory.class, forumDirectory.getId());
        if (currentPosition != newPosition) {
            // Shift and refresh displayed list
            WikiUtil.shiftListElement(forumDirectory.getChildren(), currentPosition, newPosition);

            // Required update, this is only refreshed on database load
            for (Node node : forumDirectory.getChildren()) {
                node.setDisplayPosition(forumDirectory.getChildren().indexOf(node));
            }
        }
        Contexts.getPageContext().set("currentDirectory", forumDirectory);
    }

}
