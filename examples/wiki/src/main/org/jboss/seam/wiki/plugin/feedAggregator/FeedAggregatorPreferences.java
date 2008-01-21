/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feedAggregator;

import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;

/**
 * @author Christian Bauer
 */
@Preferences(name = "FeedAggregator", description = "#{messages['feedAggregator.preferences.Name']}")
public class FeedAggregatorPreferences {

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.Title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.Urls']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 4000)
    private String urls;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.NumberOfFeedEntries']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 99l)
    @NotNull
    private Long numberOfFeedEntries;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.TruncateDescription']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 10l, max = 1000l)
    @NotNull
    private Long truncateDescription;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.HideDate']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideDate;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.HideAuthor']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideAuthor;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.HideFeedInfo']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideFeedInfo;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.HideDescription']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideDescription;

    @PreferenceProperty(
        description = "#{messages['feedAggregator.preferences.HideTitle']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean hideTitle;

    public String getTitle() {
        return title;
    }

    public String getUrls() {
        return urls;
    }

    public Long getNumberOfFeedEntries() {
        return numberOfFeedEntries;
    }

    public Long getTruncateDescription() {
        return truncateDescription;
    }

    public Boolean getHideDate() {
        return hideDate;
    }

    public Boolean getHideAuthor() {
        return hideAuthor;
    }

    public Boolean getHideFeedInfo() {
        return hideFeedInfo;
    }

    public Boolean getHideDescription() {
        return hideDescription;
    }

    public Boolean getHideTitle() {
        return hideTitle;
    }
}
