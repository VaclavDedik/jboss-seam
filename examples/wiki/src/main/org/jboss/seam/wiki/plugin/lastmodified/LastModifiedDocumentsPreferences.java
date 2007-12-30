package org.jboss.seam.wiki.plugin.lastmodified;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;

@Preferences(name = "LastModifiedDocuments", description = "#{messages['lastModifiedDocuments.preferences.Name']}")
public class LastModifiedDocumentsPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['lastModifiedDocuments.preferences.NumberOfItems']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long numberOfItems;

    @PreferenceProperty(
        description = "#{messages['lastModifiedDocuments.preferences.ShowUsernames']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE}
    )
    @NotNull
    private Boolean showUsernames;

    @PreferenceProperty(
        description = "#{messages['lastModifiedDocuments.preferences.DocumentTitleLength']}",
        visibility = {PreferenceVisibility.SYSTEM, PreferenceVisibility.INSTANCE},
        editorIncludeName = "NumberRange"
    )
    @Range(min = 5l, max = 100l)
    @NotNull
    private Long documentTitleLength;

    public Long getNumberOfItems() {
        return numberOfItems;
    }

    public Boolean getShowUsernames() {
        return showUsernames;
    }

    public Long getDocumentTitleLength() {
        return documentTitleLength;
    }
}
