/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirToc;

import org.jboss.seam.wiki.preferences.annotations.Preferences;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.hibernate.validator.Length;

/**
 * @author Christian Bauer
 */
@Preferences(name = "DirToc", description = "#{messages['dirToc.preferences.Name']}")
public class DirTocPreferences {

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.ShowRootDocuments']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showRootDocuments;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.ShowDefaultDocuments']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showDefaultDocuments;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.ShowLastUpdatedTimestamp']}",
        visibility = {PreferenceVisibility.INSTANCE}
    )
    private Boolean showLastUpdatedTimestamp;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.WithHeaderMacro']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String withHeaderMacro;

    @PreferenceProperty(
        description = "#{messages['dirToc.preferences.RootDocumentLink']}",
        visibility = {PreferenceVisibility.INSTANCE},
        editorIncludeName = "AdaptiveTextInput"
    )
    @Length(min = 0, max = 255)
    private String rootDocumentLink;

    public Boolean getShowRootDocuments() {
        return showRootDocuments;
    }

    public Boolean getShowDefaultDocuments() {
        return showDefaultDocuments;
    }

    public Boolean getShowLastUpdatedTimestamp() {
        return showLastUpdatedTimestamp;
    }

    public String getWithHeaderMacro() {
        return withHeaderMacro;
    }

    public String getRootDocumentLink() {
        return rootDocumentLink;
    }
}
