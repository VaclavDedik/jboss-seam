/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feedTeasers;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.preferences.PreferenceValueTemplate;
import org.jboss.seam.wiki.preferences.metamodel.PreferenceEntity;
import org.jboss.seam.wiki.preferences.metamodel.PreferencesSupport;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("feedTeasersPreferencesSupport")
public class FeedTeasersPreferencesSupport extends PreferencesSupport {

    public Set<PreferenceEntity> getPreferenceEntities() {
        return new HashSet<PreferenceEntity>() {{
            add( createPreferenceEntity(FeedTeasersPreferences.class) );
        }};
    }

    @Name("feedTeasersFeedPreferenceValueTemplate")
    @Scope(ScopeType.CONVERSATION)
    public static class FeedTeasersFeedTemplate implements PreferenceValueTemplate, Serializable {

        @In
        FeedDAO feedDAO;

        List<String> feedIdentifiers;

        public List<String> getTemplateValues() {
            if (feedIdentifiers == null) {
                List<Feed> feeds = feedDAO.findFeeds();
                for (Feed feed : feeds) {
                    feedIdentifiers.add(feed.getId().toString());
                }
            }
            return feedIdentifiers;
        }

    }

}
