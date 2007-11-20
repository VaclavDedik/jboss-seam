package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("forumPreferences")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
@Preference(description = "Plugin: Forum", visibility = PreferenceVisibility.USER)
public class ForumPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.forumPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Number of topics per page", visibility = PreferenceVisibility.USER)
    @Range(min = 1l, max = 999l)
    @NotNull
    private Long topicsPerPage;

    public Long getTopicsPerPage() {
        return topicsPerPage;
    }
}
