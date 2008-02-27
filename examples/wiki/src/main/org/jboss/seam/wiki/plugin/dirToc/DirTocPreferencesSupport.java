/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirToc;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;

import java.util.Set;
import java.util.HashSet;

/**
 * @author Christian Bauer
 */
@Name("dirTocPreferencesSupport")
public class DirTocPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(DirTocPreferences.class) );
        }};
    }
}
