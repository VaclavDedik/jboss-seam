package org.jboss.seam.wiki.plugin.feedTeasers;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.ScopeType;

import java.io.Serializable;
import java.util.List;

@Name("feedTeasersPlugin")
@Scope(ScopeType.PAGE)
public class FeedTeasers implements Serializable {

    @In
    FeedDAO feedDAO;

    @In("#{feedTeasersPreferences.properties['numberOfTeasers']}")
    private Long numberOfTeasers;

    @In("#{feedTeasersPreferences.properties['feedIdentifier']}")
    private Long feedIdentifier;

    private List<FeedEntry> teasers;

    public List<FeedEntry> getTeasers() {
        if (teasers  == null) loadTeasers();
        return teasers;
    }

    @Observer("Preferences.feedTeasersPreferences")
    public void loadTeasers() {
        Feed feed = feedDAO.findFeed(feedIdentifier);
        teasers = feedDAO.findFeedEntries(feed, numberOfTeasers.intValue());
    }

}
