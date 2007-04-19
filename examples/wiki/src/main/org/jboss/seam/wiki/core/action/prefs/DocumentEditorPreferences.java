package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("docEditorPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "A. Document Editor", visibility = PreferenceVisibility.USER)
public class DocumentEditorPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Preference(description = "01. Enable 'Minor Revision' checkbox by default", visibility = PreferenceVisibility.USER)
    private Boolean minorRevisionEnabled;

    @Preference(description = "02. Rows shown in text editor (with disabled preview)", visibility = PreferenceVisibility.SYSTEM)
    @Range(min = 5l, max = 100l)
    private Long regularEditAreaRows;

    @Preference(description = "03. Columns shown in text editor (with disabled preview)", visibility = PreferenceVisibility.USER)
    @Range(min = 5l, max = 250l)
    private Long regularEditAreaColumns;

    @Preference(description = "04. Rows shown in text editor (with enabled preview)", visibility = PreferenceVisibility.USER)
    @Range(min = 5l, max = 100l)
    private Long previewEditAreaRows;

    @Preference(description = "05. Columns shown in text editor (with enabled preview)", visibility = PreferenceVisibility.USER)
    @Range(min = 5l, max = 250l)
    private Long previewEditAreaColumns;

}
