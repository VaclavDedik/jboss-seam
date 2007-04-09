package org.jboss.seam.wiki.plugin.feedTeasers;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("feedTeasersPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "D. Plugin: Feed Teasers", visibility = PreferenceVisibility.INSTANCE)
public class FeedTeasersPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.feedTeasersPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Title of teaser box", visibility = PreferenceVisibility.INSTANCE)
    private String teaserTitle;

    @Preference(description = "02. Feed identifier (feedId)", visibility = PreferenceVisibility.INSTANCE)
    private Long feedIdentifier;

    @Preference(description = "03. Number of feed entries shown in list", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 3l, max = 25l)
    private Long numberOfTeasers;

    @Preference(description = "04. Truncate teaser text after characters", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 10l, max = 500l)
    private Long truncateDescription;

    @Preference(description = "05. Show author name", visibility = PreferenceVisibility.INSTANCE)
    private boolean showAuthor;
}
