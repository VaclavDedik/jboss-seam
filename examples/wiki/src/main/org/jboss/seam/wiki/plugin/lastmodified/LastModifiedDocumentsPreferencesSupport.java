package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

import java.util.HashSet;
import java.util.Set;

@Name("lastModifiedDocumentsPreferencesSupport")
public class LastModifiedDocumentsPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(LastModifiedDocumentsPreferences.class) );
        }};
    }

}
