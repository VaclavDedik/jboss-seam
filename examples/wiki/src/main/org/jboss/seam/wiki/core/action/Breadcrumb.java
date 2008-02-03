package org.jboss.seam.wiki.core.action;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Name("breadcrumbFactory")
@Scope(ScopeType.PAGE)
public class Breadcrumb implements Serializable {

    @Logger
    Log log;

    @In(required = false)
    WikiNode currentLocation;

    @Factory(value = "breadcrumb", autoCreate = true)
    public List<WikiNode> unwrap() {
        // TODO: Maybe a nested set query would be more efficient?
        log.debug("breadcrumb starting at current location: " + currentLocation);
        List<WikiNode> currentPath = new ArrayList<WikiNode>();
        if (currentLocation == null) return currentPath;
        addToPath(currentPath, currentLocation);
        Collections.reverse(currentPath);
        return currentPath;
    }

    protected void addToPath(List<WikiNode> path, WikiNode currentLocation) {
        if (Identity.instance().hasPermission("Node", "read", currentLocation) &&
            currentLocation.getId() != null && !isRootWikiNode(currentLocation) ) {
            log.debug("adding to breadcrumb: " + currentLocation);
            path.add(currentLocation);

        }
        if (currentLocation.getParent() != null ) {
            addToPath(path, currentLocation.getParent());
        }
    }

    private boolean isRootWikiNode(WikiNode node) {
        return (node.isInstance(WikiDirectory.class) && node.getId().equals(((WikiDirectory) Component.getInstance("wikiRoot")).getId()));
    }

}
