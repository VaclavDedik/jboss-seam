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
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
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
    Directory currentDirectory;

    @In
    NodeDAO nodeDAO;

    @In
    DirMenuPreferences dirMenuPreferences;

    NestedSetNodeWrapper<Node> root;

    public NestedSetNodeWrapper<Node> getRoot() {
        if (root == null) {
            refreshRoot();
        }
        return root;
    }

    @Observer("PreferenceComponent.refresh.dirMenuPreferences")
    public void refreshRoot() {
        root = nodeDAO.findMenuItems(currentDirectory, dirMenuPreferences.getMenuDepth(), dirMenuPreferences.getMenuLevels(), false);
    }
}
