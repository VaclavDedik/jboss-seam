package org.jboss.seam.wiki.plugin.flash;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.preferences.Preference;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.PreferenceSupport;
import org.hibernate.validator.Range;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Length;

import java.io.Serializable;

@Name("flashPreferences")
@Scope(ScopeType.CONVERSATION)
@Preference(description = "Plugin: Flash", visibility = PreferenceVisibility.INSTANCE)
@AutoCreate
public class FlashPreferences extends PreferenceSupport implements Serializable {

    public String getCurrentUserVariable() { return "currentUser"; }
    public String getCurrentInstanceVariable() { return "renderedBaseDocument"; }

//    @Observer(value = {"PreferenceEditor.refresh.flashPreferences", "Render.startRenderDocument"}, create = false)
    public void refreshProperties() { super.refreshProperties(); }

    @Preference(description = "01. URL of flash movie", visibility = PreferenceVisibility.INSTANCE)
    @Length(min = 0, max = 1024)
    private String flashURL;

    @Preference(description = "02. Width of embedded flash object (pixel)", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 2000l)
    @NotNull
    private Long objectWidth;

    @Preference(description = "03. Height of embedded flash object (pixel)", visibility = PreferenceVisibility.INSTANCE)
    @Range(min = 1l, max = 2000l)
    @NotNull
    private Long objectHeight;

    @Preference(description = "04. Comma-separated list of allowed domain names", visibility = PreferenceVisibility.SYSTEM)
    @Length(min = 3, max = 1024)
    private String allowedDomains;

    public String getFlashURL() {
        return flashURL;
    }

    public Long getObjectWidth() {
        return objectWidth;
    }

    public Long getObjectHeight() {
        return objectHeight;
    }

    public String getAllowedDomains() {
        return allowedDomains;
    }
}
