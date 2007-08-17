/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirMenu;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Name("dirMenuPreferences")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Plugin: Directory Menu", visibility = PreferenceVisibility.INSTANCE)
public class DirMenuPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.dirMenuPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Flatten display of menu tree to levels", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 100l)
    @NotNull
    private Long menuLevels;

    @Preference(description = "02. Maximum depth of menu tree nodes", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 100l)
    @NotNull
    private Long menuDepth;

    public Long getMenuLevels() {
        return menuLevels;
    }

    public Long getMenuDepth() {
        return menuDepth;
    }
}
