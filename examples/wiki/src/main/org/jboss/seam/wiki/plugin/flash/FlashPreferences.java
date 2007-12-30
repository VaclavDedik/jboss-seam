package org.jboss.seam.wiki.plugin.flash;

import org.hibernate.validator.Length;
import org.hibernate.validator.Range;
import org.jboss.seam.wiki.preferences.PreferenceVisibility;
import org.jboss.seam.wiki.preferences.annotations.PreferenceProperty;
import org.jboss.seam.wiki.preferences.annotations.Preferences;

import java.io.Serializable;

@Preferences(name = "Flash", description = "#{messages['flash.preferences.Name']}")
public class FlashPreferences implements Serializable {

    @PreferenceProperty(
        description = "#{messages['flash.preferences.FlashURL']}",
        visibility = PreferenceVisibility.INSTANCE
    )
    @Length(min = 0, max = 1024)
    private String url;

    @PreferenceProperty(
        description = "#{messages['flash.preferences.Width']}",
        visibility = PreferenceVisibility.INSTANCE
    )
    @Range(min = 1l, max = 2000l)
    private Long width;

    @PreferenceProperty(
        description = "#{messages['flash.preferences.Height']}",
        visibility = PreferenceVisibility.INSTANCE
    )
    @Range(min = 1l, max = 2000l)
    private Long height;

    @PreferenceProperty(
        description = "#{messages['flash.preferences.AllowedDomains']}",
        visibility = PreferenceVisibility.SYSTEM
    )
    @Length(min = 3, max = 1024)
    private String allowedDomains;

    public String getUrl() {
        return url;
    }

    public Long getWidth() {
        return width;
    }

    public Long getHeight() {
        return height;
    }

    public String getAllowedDomains() {
        return allowedDomains;
    }
}
