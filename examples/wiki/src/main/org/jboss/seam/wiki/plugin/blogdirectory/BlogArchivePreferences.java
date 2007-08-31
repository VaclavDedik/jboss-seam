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

@Name("blogArchivePreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Plugin: Blog Archive", visibility = PreferenceVisibility.INSTANCE)
public class BlogArchivePreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.blogArchivePreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Show 'Subscribe' icon", visibility = PreferenceVisibility.INSTANCE)
    private Boolean showSubscribeIcon;

    public Boolean getShowSubscribeIcon() {
        return showSubscribeIcon;
    }
}