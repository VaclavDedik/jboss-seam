/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.docPager;

import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Preferences(name = "DocPager", description = "#{messages['docPager.preferences.Name']}")
public class DocPagerPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['docPager.preferences.ByProperty']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String byProperty;

    @PreferenceProperty(
        description = "#{messages['docPager.preferences.ShowNames']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showNames;

    public String getByProperty() {
        return byProperty;
    }

    public Boolean getShowNames() {
        return showNames;
    }
}
