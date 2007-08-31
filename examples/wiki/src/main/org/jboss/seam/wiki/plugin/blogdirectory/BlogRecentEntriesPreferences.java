package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

@Name("blogRecentEntriesPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Plugin: Blog Recent Entries", visibility = PreferenceVisibility.INSTANCE)
public class BlogRecentEntriesPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.blogRecentEntriesPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Number of items in list", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 100l)
    @NotNull
    private Long recentHeadlines;

    @Preference(description = "02. Truncate item text after characters", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 10l, max = 1000l)
    @NotNull
    private Long truncateItemText;

    @Preference(description = "03. Show 'Subscribe' icon", visibility = PreferenceVisibility.INSTANCE)
    private Boolean showSubscribeIcon;

    public Long getRecentHeadlines() {
        return recentHeadlines;
    }

    public Long getTruncateItemText() {
        return truncateItemText;
    }

    public Boolean getShowSubscribeIcon() {
        return showSubscribeIcon;
    }
}