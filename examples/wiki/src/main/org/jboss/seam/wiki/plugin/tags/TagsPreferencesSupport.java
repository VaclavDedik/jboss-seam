/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.tags;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Christian Bauer
 */
@Name("tagsPreferencesSupport")
public class TagsPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(TagsPreferences.class) );
        }};
    }
}
