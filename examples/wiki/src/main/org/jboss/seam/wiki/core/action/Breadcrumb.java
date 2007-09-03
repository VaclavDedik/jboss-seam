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

    @In(required = false)
    Node currentLocation;

    @Factory(value = "breadcrumb", autoCreate = true)
    public List<Node> unwrap() {
        // TODO: Maybe a nested set query would be more efficient?
        List<Node> currentPath = new ArrayList<Node>();
        if (currentLocation == null) return currentPath;
        addToPath(currentPath, currentLocation);
        Collections.reverse(currentPath);
        return currentPath;
    }

    protected void addToPath(List<Node> path, Node currentLocation) {
        if (Identity.instance().hasPermission("Node", "read", currentLocation) &&
            currentLocation.getId() != null &&
            !currentLocation.getId().equals( ((Directory) Component.getInstance("wikiRoot")).getId() ) )
            path.add(currentLocation);
        if (currentLocation.getParent() != null ) {
            addToPath(path, currentLocation.getParent());
        }
    }

}
