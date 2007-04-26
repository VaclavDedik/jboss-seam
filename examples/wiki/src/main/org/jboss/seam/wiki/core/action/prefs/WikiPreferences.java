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

import java.io.Serializable;

@Name("wikiPreferences")
@AutoCreate
@Scope(ScopeType.CONVERSATION)
@Preference(description = "A. Wiki Preferences", visibility = PreferenceVisibility.SYSTEM)
public class WikiPreferences extends PreferenceSupport implements Serializable {

    @Preference(description = "01. Base URL without trailing slash (e.g. 'http://my.wiki.server/installdir')", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 8, max = 255)
    private String baseUrl;

    @Preference(description = "02. Timezone of server (ID as defined in java.util.TimeZone)", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 63)
    private String timeZone;

    @Preference(description = "03. Theme directory name", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 20)
    private String themeName;

    @Preference(description = "04. Identifier of member area (home directories)", visibility = PreferenceVisibility.SYSTEM)
    private Long memberAreaId;

    @Preference(description = "05. Identifier of the default start document of the Wiki", visibility = PreferenceVisibility.SYSTEM)
    private Long defaultDocumentId;

    @Preference(description = "06. Render all links as permanent numeric identifier links (or as /Wiki/WordLinks)", visibility = PreferenceVisibility.SYSTEM)
    private boolean renderPermlinks;

    @Preference(description = "07. Append this suffix to permanent identifier", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 2, max = 20)
    @org.hibernate.validator.Pattern(regex="\\.[a-zA-z]+")
    private String permlinkSuffix;

    @Preference(description = "08. Purge feed entries after N days", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 1l, max = 999l)
    private Long purgeFeedEntriesAfterDays;

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

    public boolean isRenderPermlinks() {
        return renderPermlinks;
    }

    public String getPermlinkSuffix() {
        return permlinkSuffix;
    }

    public Long getPurgeFeedEntriesAfterDays() {
        return purgeFeedEntriesAfterDays;
    }

}
