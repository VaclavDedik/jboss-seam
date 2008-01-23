/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.connectors.feed;

import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.util.*;
import java.io.Serializable;
import java.net.URL;

/**
 * @author Christian Bauer
 */
@Name("feedAggregatorDAO")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class FeedAggregatorDAO implements Serializable {

    @Logger
    Log log;

    @In("feedConnectorCache")
    FeedConnector feedConnector;

    @In
    FeedAggregateCache feedAggregateCache;

    public List<FeedEntryDTO> getLatestFeedEntries(int numberOfFeedEntries, URL[] feedURLs) {
        return getLatestFeedEntries(numberOfFeedEntries, feedURLs, null);
    }

    public List<FeedEntryDTO> getLatestFeedEntries(int numberOfFeedEntries, URL[] feedURLs, String aggregateId) {
        if (feedURLs == null) return Collections.EMPTY_LIST;

        List<FeedEntryDTO> feedEntries = new ArrayList<FeedEntryDTO>();

        for (URL feedURL : feedURLs) {
            // For each feed, get the feed entries and put them in a sorted collection,
            // so we get overall sorting
            log.debug("retrieving feed entries from connector for feed URL: " + feedURL);
            List<FeedEntryDTO> result = feedConnector.getFeedEntries(feedURL.toString());
            log.debug("retrieved feed entries: " + result.size());
            feedEntries.addAll(result);
            log.debug("number of aggregated feed entries so far: " + feedEntries.size());
        }

        Collections.sort(
            feedEntries,
            // Sort by date of feed entry ascending
            new Comparator<FeedEntryDTO>() {
                public int compare(FeedEntryDTO a, FeedEntryDTO b) {
                    if (a.getFeedEntry().getUpdatedDate() != null && b.getFeedEntry().getUpdatedDate() != null) {
                        if (a.getFeedEntry().getUpdatedDate().getTime() >
                            b.getFeedEntry().getUpdatedDate().getTime()) return -1;

                        return (a.getFeedEntry().getUpdatedDate().getTime() ==
                                b.getFeedEntry().getUpdatedDate().getTime() ? 0 : 1);

                    } else {
                        if (a.getFeedEntry().getPublishedDate().getTime() >
                            b.getFeedEntry().getPublishedDate().getTime()) return -1;

                        return (a.getFeedEntry().getPublishedDate().getTime() ==
                                b.getFeedEntry().getPublishedDate().getTime() ? 0 : 1);
                    }
                }
            }
        );

        if (aggregateId != null) {
            log.debug("caching aggregated feed entries under id: " + aggregateId);
            // Cache the result for later requests through FeedServlet (by aggregateId)
            feedAggregateCache.put(aggregateId, feedEntries);
        }

        return feedEntries.size() > numberOfFeedEntries
                ? new ArrayList<FeedEntryDTO>(feedEntries).subList(0, numberOfFeedEntries)
                : new ArrayList<FeedEntryDTO>(feedEntries);
    }
}
