package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;

import java.io.Serializable;

@Name("commentsPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Core: Visitor Comments", visibility = PreferenceVisibility.USER)
@AutoCreate
public class CommentsPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.commentsPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. List flat comments ascending by date (or descending)", visibility = PreferenceVisibility.SYSTEM)
    private Boolean listAscending;

    @Preference(description = "02. Enable comments by default", visibility = PreferenceVisibility.USER)
    private Boolean enableByDefault;

    @Preference(description = "03. Threaded comments (or flat)", visibility = PreferenceVisibility.SYSTEM)
    private Boolean threadedComments;

    public Boolean getListAscending() {
        return listAscending;
    }

    public Boolean getEnableByDefault() {
        return enableByDefault;
    }

    public Boolean getThreadedComments() {
        return threadedComments;
    }
}
