package org.jboss.seam.wiki.plugin.feedTeasers;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.hibernate.validator.Length;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(name = "FeedTeasers", description = "#{messages['feedTeasers.preferences.Name']}")
public class FeedTeasersPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['feedTeasers.preferences.Title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['feedTeasers.preferences.Feed']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "SelectOne",
        templateComponentName = "feedTeasersFeedPreferenceValueTemplate"
    )
    private Long feed;

    @PreferenceProperty(
        description = "#{messages['feedTeasers.preferences.NumberOfTeasers']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long numberOfTeasers;

    @PreferenceProperty(
        description = "#{messages['feedTeasers.preferences.TruncateDescription']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 10l, max = 5000l)
    @NotNull
    private Long truncateDescription;

    @PreferenceProperty(
        description = "#{messages['feedTeasers.preferences.ShowAuthor']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showAuthor;

    public String getTitle() {
        return title;
    }

    public Long getFeed() {
        return feed;
    }

    public Long getNumberOfTeasers() {
        return numberOfTeasers;
    }

    public Long getTruncateDescription() {
        return truncateDescription;
    }

    public Boolean getShowAuthor() {
        return showAuthor;
    }
}
