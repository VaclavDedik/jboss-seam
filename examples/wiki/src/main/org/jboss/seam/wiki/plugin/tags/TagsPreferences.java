/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.tags;

import org.hibernate.validator.NotNull;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

/**
 * @author Christian Bauer
 */
@Preferences(name = "Tags", description = "#{messages['tags.preferences.Name']}")
public class TagsPreferences {

    @PreferenceProperty(
        description = "#{messages['tags.preferences.LinkToCurrentDocument']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean linkToCurrentDocument;

    public Boolean getLinkToCurrentDocument() {
        return linkToCurrentDocument;
    }
}
