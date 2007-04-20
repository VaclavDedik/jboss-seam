package org.jboss.seam.wiki.plugin.flash;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;

import java.io.Serializable;

@Name("flashPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "D. Plugin: Flash", visibility = PreferenceVisibility.INSTANCE)
public class FlashPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "currentDocument"; }

    @Observer("PreferenceEditor.refresh.flashPreferences")
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. URL of flash movie", visibility = PreferenceVisibility.INSTANCE)
    private String flashURL;

    @Preference(description = "02. Width of embedded flash object (pixel)", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 2000l)
    private Long objectWidth;

    @Preference(description = "03. Height of embedded flash object (pixel)", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 2000l)
    private Long objectHeight;

}
