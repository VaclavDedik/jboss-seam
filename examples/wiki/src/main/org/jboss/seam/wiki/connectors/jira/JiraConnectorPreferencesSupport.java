/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.jira;

import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;
import org.jboss.seam.annotations.Name;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Christian Bauer
 */
@Name("jiraConnectorPreferencesSupport")
public class JiraConnectorPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(JiraConnectorPreferences.class) );
        }};
    }
}
