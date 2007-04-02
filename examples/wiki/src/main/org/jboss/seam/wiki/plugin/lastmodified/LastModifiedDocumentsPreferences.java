package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("lastModifiedDocumentsPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "B. Plugin: Last Modified", visibility = PreferenceVisibility.INSTANCE)
public class LastModifiedDocumentsPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.lastModifiedDocumentsPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. Number of items shown in list", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 3l, max = 25l)
    private Long numberOfItems;

    @Preference(description = "02. Show user names", visibility = PreferenceVisibility.INSTANCE)
    private Boolean showUsernames;

    @Preference(description = "03. Truncate document titles after characters", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 5l, max = 100l)
    private Long documentTitleLength;


}
