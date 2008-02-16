/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.tags;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
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

    @PreferenceProperty(
        description = "#{messages['tags.preferences.MaxNumberOfTags']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 99l)
    private Long maxNumberOfTags;

    @PreferenceProperty(
        description = "#{messages['tags.preferences.MinimumCount']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 1l, max = 99l)
    private Long minimumCount;

    @PreferenceProperty(
        description = "#{messages['tags.preferences.Cloud']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean cloud;

    public Boolean getLinkToCurrentDocument() {
        return linkToCurrentDocument;
    }

    public Long getMaxNumberOfTags() {
        return maxNumberOfTags;
    }

    public Long getMinimumCount() {
        return minimumCount;
    }

    public Boolean getCloud() {
        return cloud;
    }
}
