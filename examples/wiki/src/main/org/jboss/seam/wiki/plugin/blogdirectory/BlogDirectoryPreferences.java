package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("blogDirectoryPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "C. Plugin: Blog Directory", visibility = PreferenceVisibility.INSTANCE)
public class BlogDirectoryPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.blogDirectoryPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Number of blog entries per page", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 100l)
    private Long pageSize;

}
