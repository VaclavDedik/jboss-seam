/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirMenu;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;

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

    @In("#{preferences.get('DirMenu', currentMacro)}")
    DirMenuPreferences prefs;
        
    NestedSetNodeWrapper<WikiDirectory> root;

    public NestedSetNodeWrapper<WikiDirectory> getRoot() {
        if (root == null) refreshRoot();
        return root;
    }

    @Observer(value = "Macro.render.dirMenu", create = false)
    public void refreshRoot() {

        if (prefs.getOnlyMenuItems() != null && prefs.getOnlyMenuItems()) {
            root = wikiNodeDAO.findMenuItemTree(currentDirectory, 3l, 3l, false);
        } else {
            root = wikiNodeDAO.findWikiDirectoryTree(currentDirectory, 3l, 3l, false);
        }

    }
}
