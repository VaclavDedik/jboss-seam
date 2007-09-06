/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.Length;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

@Name("wikiPreferences")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Core: Wiki Preferences", visibility = PreferenceVisibility.SYSTEM)
public class WikiPreferences extends PreferenceSupport implements Serializable {

    @Preference(description = "01. Base URL without trailing slash (e.g. 'http://my.wiki.server/installdir')", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 8, max = 255)
    @NotNull
    private String baseUrl;

    @Preference(description = "02. Timezone of server (ID as defined in java.util.TimeZone)", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 63)
    @NotNull
    private String timeZone;

    @Preference(description = "03. Theme directory name", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 20)
    @NotNull
    private String themeName;

    @Preference(description = "04. Identifier of member area (home directories)", visibility = PreferenceVisibility.SYSTEM)
    @NotNull
    private Long memberAreaId;

    @Preference(description = "05. Identifier of the default start document of the Wiki", visibility = PreferenceVisibility.SYSTEM)
    @NotNull
    private Long defaultDocumentId;

    @Preference(description = "06. Render all links as permanent numeric identifier links (or as /Wiki/WordLinks)", visibility = PreferenceVisibility.SYSTEM)
    private Boolean renderPermlinks;

    @Preference(description = "07. Append this suffix to permanent identifier", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 2, max = 20)
    @org.hibernate.validator.Pattern(regex="\\.[a-zA-z]+")
    @NotNull
    private String permlinkSuffix;

    @Preference(description = "08. Purge feed entries after N days", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 999l)
    @NotNull
    private Long purgeFeedEntriesAfterDays;

    @Preference(description = "09. Replace @ symbol in e-mail addresses with", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 1, max = 20)
    @NotNull
    private String atSymbolReplacement;

    @Preference(description = "10. Flatten main menu to levels", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 5l)
    @NotNull
    private Long mainMenuLevels;

    @Preference(description = "11. Maximum depth of main menu nodes", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 99l)
    @NotNull
    private Long mainMenuDepth;

    @Preference(description = "12. Show only nodes owned by system administrator in main menu", visibility = PreferenceVisibility.SYSTEM)
    private Boolean mainMenuShowAdminOnly;

    @Preference(description = "13. Show document creator/edit history in document footer", visibility = PreferenceVisibility.SYSTEM)
    private Boolean showDocumentCreatorHistory;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getThemeName() {
        return themeName;
    }

    public Long getMemberAreaId() {
        return memberAreaId;
    }

    public Long getDefaultDocumentId() {
        return defaultDocumentId;
    }

    public Boolean isRenderPermlinks() {
        return renderPermlinks;
    }

    public String getPermlinkSuffix() {
        return permlinkSuffix;
    }

    public Long getPurgeFeedEntriesAfterDays() {
        return purgeFeedEntriesAfterDays;
    }

    public String getAtSymbolReplacement() {
        return atSymbolReplacement;
    }

    public Long getMainMenuLevels() {
        return mainMenuLevels;
    }

    public Long getMainMenuDepth() {
        return mainMenuDepth;
    }

    public Boolean isMainMenuShowAdminOnly() {
        return mainMenuShowAdminOnly;
    }

    public Boolean getShowDocumentCreatorHistory() {
        return showDocumentCreatorHistory;
    }
}
