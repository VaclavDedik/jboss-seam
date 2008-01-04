package org.jboss.seam.wiki.plugin.forum;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Preferences(name = "Forum", description = "#{messages['forum.preferences.Name']}")
public class ForumPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['forum.preferences.TopicsPerPage']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.USER},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 100l)
    @NotNull
    private Long topicsPerPage;

    @PreferenceProperty(
        description = "#{messages['forum.preferences.NotifyMeOfReplies']}",
        visibility = PreferenceVisibility.USER
    )
    private Boolean notifyMeOfReplies;

    public Long getTopicsPerPage() {
        return topicsPerPage;
    }

    public Boolean getNotifyMeOfReplies() {
        return notifyMeOfReplies;
    }
}
