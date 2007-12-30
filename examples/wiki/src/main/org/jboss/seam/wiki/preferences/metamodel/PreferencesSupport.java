/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.preferences.metamodel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;

import java.util.Set;

@Scope(ScopeType.APPLICATION)
public abstract class PreferencesSupport {

    @Observer("Preferences.addPreferencesSupport")
    public void add(Set<PreferencesSupport> preferencesSupportComponents) {
        preferencesSupportComponents.add(this);
    }

    public abstract Set<PreferenceEntity> getPreferenceEntities();

    public PreferenceEntity createPreferenceEntity(Class preferenceEntityClass) {
        return new PreferenceEntity(preferenceEntityClass);
    }


}