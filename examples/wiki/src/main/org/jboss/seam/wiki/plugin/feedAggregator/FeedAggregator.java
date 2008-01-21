/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.feedAggregator;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.connectors.feed.FeedAggregatorDAO;
import org.jboss.seam.wiki.connectors.feed.FeedEntryDTO;

import java.util.List;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("feedAggregator")
@Scope(ScopeType.PAGE)
public class FeedAggregator implements Serializable {

    @Logger
    Log log;

    @In
    FeedAggregatorDAO feedAggregatorDAO;

    @In("#{preferences.get('FeedAggregator', currentMacro)}")
    FeedAggregatorPreferences prefs;

    private List<FeedEntryDTO> feedEntries;

    public List<FeedEntryDTO> getFeedEntries() {
        if (feedEntries == null) loadFeedEntries();
        return feedEntries;
    }

    @Observer(value = "Macro.render.feedAggregator", create = false)
    public void loadFeedEntries() {

        if (prefs.getUrls() == null || prefs.getUrls().length() < 8) return;

        // Split the URLs by space
        String[] urls = prefs.getUrls().split(" ");

        // First check if the URLs are valid, if not we might as well just skip it...
        List<String> validUrls = new ArrayList<String>();
        for (String url : urls) {
            try {
                URL testUrl = new URL(url);
                if (!testUrl.getProtocol().equals("http")) {
                    log.debug("skipping URL with unsupported protocol: " + url);
                    continue;
                }
            } catch (MalformedURLException e) {
                log.debug("skipping invalid URL: " + url);
                continue;
            }
            validUrls.add(url);
        }

        log.debug("aggregating feeds: " + validUrls.size());
        feedEntries =
            feedAggregatorDAO.getLatestFeedEntries(
                prefs.getNumberOfFeedEntries().intValue(),
                validUrls.toArray(new String[validUrls.size()])
            );
    }

}
