package org.jboss.seam.wiki.plugin.feedTeasers;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;

import java.io.Serializable;

@Name("feedTeasersPreferences")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Preference(description = "Plugin: Feed Teasers", visibility = PreferenceVisibility.INSTANCE)
public class FeedTeasersPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.feedTeasersPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Title of teaser box", visibility = PreferenceVisibility.INSTANCE)
    private String teaserTitle;

    @Preference(description = "02. Feed identifier (feedId)", visibility = PreferenceVisibility.INSTANCE)
    @NotNull
    private Long feedIdentifier;

    @Preference(description = "03. Number of feed entries shown in list", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 3l, max = 25l)
    @NotNull
    private Long numberOfTeasers;

    @Preference(description = "04. Truncate teaser text after characters", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 10l, max = 5000l)
    @NotNull
    private Long truncateDescription;

    @Preference(description = "05. Show author name", visibility = PreferenceVisibility.INSTANCE)
    private Boolean showAuthor;

    public String getTeaserTitle() {
        return teaserTitle;
    }

    public Long getFeedIdentifier() {
        return feedIdentifier;
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
