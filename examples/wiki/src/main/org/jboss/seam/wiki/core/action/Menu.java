/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;

import java.io.Serializable;

/**
 * Holds the nodes that are displayed in the site menu
 *
 * @author Christian Bauer
 */
@Name("menu")
@Scope(ScopeType.PAGE)
public class Menu implements Serializable {

    @Logger
    Log log;

    @In
    Directory wikiRoot;

    @In
    NodeDAO nodeDAO;

    @In
    WikiPreferences wikiPreferences;

    NestedSetNodeWrapper<Node> root;
    public NestedSetNodeWrapper<Node> getRoot() {
        if (root == null) {
            refreshRoot();
        }
        return root;
    }

    @Observer(value = "Nodes.menuStructureModified", create = false)
    public void refreshRoot() {
        log.debug("Loading menu items tree");
        root = nodeDAO.findMenuItems(
                wikiRoot,
                wikiPreferences.getMainMenuDepth(), 
                wikiPreferences.getMainMenuLevels(),
                wikiPreferences.isMainMenuShowAdminOnly()
        );
    }
}
