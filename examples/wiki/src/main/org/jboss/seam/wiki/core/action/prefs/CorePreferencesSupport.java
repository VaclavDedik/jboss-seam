package org.jboss.seam.wiki.core.action.prefs;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;

import java.util.HashSet;
import java.util.Set;

@Name("corePreferencesSupport")
public class CorePreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(WikiPreferences.class) );
            add( createPreferenceEntity(UserManagementPreferences.class) );
            add( createPreferenceEntity(DocumentEditorPreferences.class) );
            add( createPreferenceEntity(CommentsPreferences.class) );
        }};
    }

}
