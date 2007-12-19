/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirMenu;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.ScopeType;

import java.io.Serializable;

/**
 * Menu tree, base is the current directory.
 *
 * @author Christian Bauer
 */
@Name("dirMenu")
@Scope(ScopeType.PAGE)
public class DirMenu implements Serializable {

    @In
    WikiDirectory currentDirectory;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    DirMenuPreferences dirMenuPreferences;
/*
    NestedSetNodeWrapper<WikiDirectoryNSDelegate> root;

    public NestedSetNodeWrapper<WikiDirectoryNSDelegate> getRoot() {
        if (root == null) {
            refreshRoot();
        }
        return root;
    }

    @Observer("PreferenceComponent.refresh.dirMenuPreferences")
    public void refreshRoot() {
        root = wikiNodeDAO.findWikiDirectoryTree(currentDirectory, dirMenuPreferences.getMenuDepth(), dirMenuPreferences.getMenuLevels(), false);
    }
    */
}
