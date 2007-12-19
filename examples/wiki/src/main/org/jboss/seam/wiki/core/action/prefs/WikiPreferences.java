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

    @Preference(description = "04. Wiki area for user home directorie", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 1000)
    @NotNull
    private String memberArea;

    @Preference(description = "05. Wiki area containing help texts", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 1000)
    @NotNull
    private String helpArea;

    @Preference(description = "06. Identifier of the default start document of the Wiki", visibility = PreferenceVisibility.SYSTEM)
    @NotNull
    private Long defaultDocumentId;

    @Preference(description = "07. Render all links as permanent numeric identifier links (or as /Wiki/WordLinks)", visibility = PreferenceVisibility.SYSTEM)
    private Boolean renderPermlinks;

    @Preference(description = "08. Append this suffix to permanent identifier", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 2, max = 20)
    @org.hibernate.validator.Pattern(regex="\\.[a-zA-z]+")
    @NotNull
    private String permlinkSuffix;

    @Preference(description = "09. Feed title prefix", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 0, max = 255)
    @NotNull
    private String feedTitlePrefix;

    @Preference(description = "10. Purge feed entries after N days", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 999l)
    @NotNull
    private Long purgeFeedEntriesAfterDays;

    @Preference(description = "11. Replace @ symbol in e-mail addresses with", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 1, max = 20)
    @NotNull
    private String atSymbolReplacement;

    @Preference(description = "12. Flatten main menu to levels (set to 0 for unlimited visible levels)", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 0l, max = 10l)
    @NotNull
    private Long mainMenuLevels;

    @Preference(description = "13. Maximum depth of main menu nodes", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 99l)
    @NotNull
    private Long mainMenuDepth;

    @Preference(description = "14. Show only nodes owned by system administrator in main menu", visibility = PreferenceVisibility.SYSTEM)
    private Boolean mainMenuShowAdminOnly;

    @Preference(description = "15. Show document creator/edit history in document footer", visibility = PreferenceVisibility.SYSTEM)
    private Boolean showDocumentCreatorHistory;
    
    @Preference(description = "16. Show document tags in document footer", visibility = PreferenceVisibility.SYSTEM)
    private Boolean showTags;

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getThemeName() {
        return themeName;
    }

    public String getMemberArea() {
        return memberArea;
    }

    public String getHelpArea() {
        return helpArea;
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

    public String getFeedTitlePrefix() {
        return feedTitlePrefix;
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

    public Boolean getShowTags() {
        return showTags;
    }

}
