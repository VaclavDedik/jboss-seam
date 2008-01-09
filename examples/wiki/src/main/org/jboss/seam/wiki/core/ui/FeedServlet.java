/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.ui;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import org.jboss.seam.Component;
import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.model.FeedEntry;
import org.jboss.seam.wiki.core.model.WikiCommentFeedEntry;
import org.jboss.seam.wiki.core.action.Authenticator;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.util.*;

/**
 * Serves syndicated feeds, one feed for each directory that has a feed.
 * <p>
 * This servlet uses either the currently logged in user (session) or
 * basic HTTP authorization if there is no user logged in or if the feed
 * requires a higher access level than currently available. The access level
 * of the feed is the read-access level of the directory the feed belongs to.
 * Feed entries are also read-access filtered, depending on the document they
 * belong to.
 *
 * @author Christian Bauer
 */
public class FeedServlet extends HttpServlet {

    public static enum Comments {
        include, exclude, only
    }

    // Possible feed types
    public enum SyndFeedType {
        ATOM("/atom.seam", "atom_1.0", "application/atom+xml"),
        RSS2("/rss.seam", "rss_2.0", "application/rss+xml");

        SyndFeedType(String pathInfo, String feedType, String contentType) {
            this.pathInfo = pathInfo;
            this.feedType = feedType;
            this.contentType = contentType;
        }
        String pathInfo;
        String feedType;
        String contentType;
    }

    // Supported feed types
    private Map<String, SyndFeedType> feedTypes = new HashMap<String,SyndFeedType>() {{
        put(SyndFeedType.ATOM.pathInfo, SyndFeedType.ATOM);
    }};

    // Allow unit testing
    public FeedServlet() {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String feedIdParam = request.getParameter("feedId");
        String tagParam = request.getParameter("tag");
        String commentsParam = request.getParameter("comments");

        Comments comments  = Comments.include;
        if (commentsParam != null) {
            try {
                comments = Comments.valueOf(commentsParam);
            } catch (IllegalArgumentException ex) {}
        }

        try {
            Long.valueOf(feedIdParam);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed " + feedIdParam);
            return;
        }

        try {
            Long.valueOf(feedIdParam);
        } catch (NumberFormatException ex) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed " + feedIdParam);
            return;
        }


        if (!feedTypes.containsKey(pathInfo)) return;
        SyndFeedType syndFeedType = feedTypes.get(pathInfo);
        if (feedIdParam == null) return;

        // TODO: Seam should use its transaction interceptor for java beans: http://jira.jboss.com/jira/browse/JBSEAM-957
        UserTransaction userTx = null;
        boolean startedTx = false;
        try {
            userTx = (UserTransaction)org.jboss.seam.Component.getInstance("org.jboss.seam.transaction.transaction");
            if (userTx.getStatus() != javax.transaction.Status.STATUS_ACTIVE) {
                startedTx = true;
                userTx.begin();
            }

            FeedDAO feedDAO = (FeedDAO)Component.getInstance("feedDAO");
            Feed feed = feedDAO.findFeed(Long.valueOf(feedIdParam));
            if (feed == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed " + feedIdParam);
                if (startedTx) userTx.commit();
                return;
            }

            // Authenticate and authorize, first with current user (session) then with basic HTTP authentication
            Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
            if (feed.getReadAccessLevel() > currentAccessLevel) {
                boolean loggedIn = ((Authenticator)Component.getInstance("authenticator")).authenticateBasicHttp(request);
                currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
                if (!loggedIn || feed.getReadAccessLevel() > currentAccessLevel) {
                    response.setHeader("WWW-Authenticate", "Basic realm=\"" + feed.getTitle().replace("\"", "'") + "\"");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    if (startedTx) userTx.commit();
                    return;
                }
            }

            SyndFeed syndFeed = createSyndFeed(request.getRequestURL().toString(), syndFeedType,  feed, currentAccessLevel, tagParam, comments);

            // Write feed to output
            response.setContentType(syndFeedType.contentType);
            response.setCharacterEncoding("UTF-8");
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, response.getWriter());
            response.getWriter().flush();

            if (startedTx) userTx.commit();
        } catch (Exception ex) {
            try {
                if (startedTx && userTx.getStatus() != javax.transaction.Status.STATUS_MARKED_ROLLBACK)
                    userTx.rollback();
            } catch (Exception rbEx) {
                rbEx.printStackTrace();
            }
            throw new RuntimeException(ex);
        }
    }

    public SyndFeed createSyndFeed(String baseURI, SyndFeedType syndFeedType, Feed feed, Integer currentAccessLevel) {
        return createSyndFeed(baseURI, syndFeedType, feed, currentAccessLevel, null, Comments.include);
    }

    public SyndFeed createSyndFeed(String baseURI, SyndFeedType syndFeedType, Feed feed, Integer currentAccessLevel, String tag, Comments comments) {

        WikiPreferences prefs = (WikiPreferences) Preferences.getInstance("Wiki");

        // Create feed
        SyndFeed syndFeed = new SyndFeedImpl();
        syndFeed.setUri(baseURI + "?feedId=" + feed.getId());
        syndFeed.setFeedType(syndFeedType.feedType);
        syndFeed.setTitle(prefs.getFeedTitlePrefix() + feed.getTitle());
        if (tag != null) {
            syndFeed.setTitle(
                syndFeed.getTitle() + " (" + Messages.instance().get("lacewiki.label.tagDisplay.Tag") + " '" + tag + "')"
            );
        }
        syndFeed.setLink(feed.getLink());
        syndFeed.setAuthor(feed.getAuthor());
        if (feed.getDescription() != null && feed.getDescription().length() >0)
            syndFeed.setDescription(feed.getDescription());
        syndFeed.setPublishedDate(feed.getPublishedDate());

        // Create feed entries
        List<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
        SortedSet<FeedEntry> entries = feed.getFeedEntries();
        for (FeedEntry entry : entries) {

            if (entry.getReadAccessLevel() > currentAccessLevel) continue;

            if (tag != null && !entry.isTagged(tag)) continue;

            if (comments.equals(Comments.exclude) && entry.isInstance(WikiCommentFeedEntry.class)) continue;
            if (comments.equals(Comments.only) && !entry.isInstance(WikiCommentFeedEntry.class)) continue;

            SyndEntry syndEntry;
            syndEntry = new SyndEntryImpl();
            syndEntry.setTitle(entry.getTitle());
            syndEntry.setLink(entry.getLink());
            syndEntry.setUri(entry.getLink());
            syndEntry.setAuthor(entry.getAuthor());
            syndEntry.setPublishedDate(entry.getPublishedDate());
            syndEntry.setUpdatedDate(entry.getUpdatedDate());

            SyndContent description;
            description = new SyndContentImpl();
            description.setType(entry.getDescriptionType());
            description.setValue(WikiUtil.removeMacros(entry.getDescriptionValue()));
            syndEntry.setDescription(description);

            syndEntries.add(syndEntry);
        }
        syndFeed.setEntries(syndEntries);

        return syndFeed;
    }

}
