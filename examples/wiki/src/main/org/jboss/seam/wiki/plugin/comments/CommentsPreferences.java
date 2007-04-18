package org.jboss.seam.wiki.plugin.comments;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("commentsPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "E. Plugin: Visitor Comments", visibility = PreferenceVisibility.INSTANCE)
public class CommentsPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.commentsPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. List comments ascending by date (or descending)", visibility = PreferenceVisibility.INSTANCE)
    private boolean listAscending;
}
