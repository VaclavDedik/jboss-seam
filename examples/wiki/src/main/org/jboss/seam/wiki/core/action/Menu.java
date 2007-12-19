/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;

import java.io.Serializable;

/**
 * Holds the nodes that are displayed in the site menu
 *
 * @author Christian Bauer
 */
@Name("menu")
@Scope(ScopeType.SESSION)
public class Menu implements Serializable {

    @Logger
    Log log;

    @In
    WikiDirectory wikiRoot;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiPreferences wikiPreferences;

    NestedSetNodeWrapper<WikiDirectory> root;
    public NestedSetNodeWrapper<WikiDirectory> getRoot() {
        if (root == null) {
            refreshRoot();
        }
        return root;
    }

    @Observer(value = { "Nodes.menuStructureModified", "PersistenceContext.filterReset" }, create = false)
    public void refreshRoot() {
        log.debug("Loading menu items tree");
        root = wikiNodeDAO.findMenuItemTree(
                wikiRoot,
                wikiPreferences.getMainMenuDepth(), 
                wikiPreferences.getMainMenuLevels(),
                wikiPreferences.isMainMenuShowAdminOnly()
        );
    }
}
