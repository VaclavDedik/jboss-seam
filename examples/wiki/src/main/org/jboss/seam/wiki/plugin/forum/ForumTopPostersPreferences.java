/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Length;

/**
 * @author Christian Bauer
 */
@Preferences(name = "ForumTopPosters", description = "#{messages['forumTopPosters.preferences.Name']}")
public class ForumTopPostersPreferences {

    @PreferenceProperty(
        description = "#{messages['forumTopPosters.preferences.Title']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    @NotNull
    private String title;

    @PreferenceProperty(
        description = "#{messages['forumTopPosters.preferences.NumberOfPosters']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 100l)
    @NotNull
    private Long numberOfPosters;

    @PreferenceProperty(
        description = "#{messages['forumTopPosters.preferences.ForumLink']}",
        visibility = PreferenceVisibility.INSTANCE,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 3, max = 255)
    private String forumLink;

    @PreferenceProperty(
        description = "#{messages['forumTopPosters.preferences.ExcludeRoles']}",
        visibility = PreferenceVisibility.INSTANCE,
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 3, max = 255)
    private String excludeRoles;

    public String getTitle() {
        return title;
    }

    public Long getNumberOfPosters() {
        return numberOfPosters;
    }

    public String getForumLink() {
        return forumLink;
    }

    public String getExcludeRoles() {
        return excludeRoles;
    }
}
