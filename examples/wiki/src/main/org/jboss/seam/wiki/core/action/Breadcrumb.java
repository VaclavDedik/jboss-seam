package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Directory;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

@Name("breadcrumbFactory")
@Scope(ScopeType.PAGE)
public class Breadcrumb implements Serializable {

    @In
    Node currentNode;

    @Factory(value = "breadcrumb", autoCreate = true)
    public List<Node> unwrap() {
        // TODO: Maybe a nested set query would be more efficient?
        List<Node> currentDirectoryPath = new ArrayList<Node>();
        addDirectoryToPath(currentDirectoryPath, currentNode);
        Collections.reverse(currentDirectoryPath);
        return currentDirectoryPath;
    }

    protected void addDirectoryToPath(List<Node> path, Node currentNode) {
        if (Identity.instance().hasPermission("Node", "read", currentNode) &&
            !currentNode.getId().equals( ((Directory) Component.getInstance("wikiRoot")).getId() ) )
            path.add(currentNode);
        if (currentNode.getParent() != null ) {
            addDirectoryToPath(path, currentNode.getParent());
        }
    }

}
