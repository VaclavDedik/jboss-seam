package org.jboss.seam.wiki.plugin.flash;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

import java.util.Set;
import java.util.HashSet;

@Name("flashPreferencesSupport")
public class FlashPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(FlashPreferences.class) );
        }};
    }

}
