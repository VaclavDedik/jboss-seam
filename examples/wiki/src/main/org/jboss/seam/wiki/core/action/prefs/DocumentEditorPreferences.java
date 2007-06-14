package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;

import java.io.Serializable;

@Name("docEditorPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Core: Document Editor", visibility = PreferenceVisibility.USER)
public class DocumentEditorPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Preference(description = "01. Enable 'Minor Revision' checkbox by default", visibility = PreferenceVisibility.USER)
    private Boolean minorRevisionEnabled;

    @Preference(description = "02. Rows shown in text editor by default", visibility = PreferenceVisibility.USER)
    @Range(min = 5l, max = 100l)
    @NotNull
    private Long regularEditAreaRows;

    @Preference(description = "03. Columns shown in text editor", visibility = PreferenceVisibility.USER)
    @Range(min = 5l, max = 250l)
    @NotNull
    private Long regularEditAreaColumns;

}
