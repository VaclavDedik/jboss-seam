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
import org.jboss.seam.Seam;
import org.jboss.seam.web.Session;
import org.jboss.seam.security.Identity;
import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.Authenticator;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeFactory;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;
import org.jboss.seam.wiki.connectors.feed.FeedAggregateCache;
import org.jboss.seam.wiki.connectors.feed.FeedEntryDTO;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

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
 * requires a higher access level than currently available. Feed entries are also
 * read-access filtered. Optionally, requests can enable/disable comments on the feed
 * or filter by tag. It's up to the actual <tt>WikiFeedEntry</tt> instance how these
 * filters are applied.
 *
 * @author Christian Bauer
 */
public class FeedServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(FeedServlet.class);

    public static enum Comments {
        include, exclude, only
    }

    // Possible feed types
    public enum SyndFeedType {
        ATOM("/atom.seam", "atom_1.0", "application/atom+xml");
        // TODO: I don't think we'll ever do that: ,RSS2("/rss.seam", "rss_2.0", "application/rss+xml");

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

        String feedIdParam = request.getParameter("feedId");
        String areaNameParam = request.getParameter("areaName");
        String nodeNameParam = request.getParameter("nodeName");
        String aggregateParam = request.getParameter("aggregate");
        log.debug(">>> feed request id: '" + feedIdParam + "' area name: '" + areaNameParam + "' node name: '" + nodeNameParam + "'");

        // Feed type
        String pathInfo = request.getPathInfo();
        log.debug("requested feed type: " + pathInfo);
        if (!feedTypes.containsKey(pathInfo)) {
            log.debug("can not render this feed type, returning BAD REQUEST");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported feed type " + pathInfo);
            return;
        }
        SyndFeedType syndFeedType = feedTypes.get(pathInfo);

        // Comments
        String commentsParam = request.getParameter("comments");
        Comments comments  = Comments.include;
        if (commentsParam != null) {
            try {
                comments = Comments.valueOf(commentsParam);
            } catch (IllegalArgumentException ex) {}
        }
        log.debug("feed rendering handles comments: " + comments);

        // Tag
        String tagParam = request.getParameter("tag");
        String tag = null;
        if (tagParam != null && tagParam.length() >0) {
            log.debug("feed rendering restricts on tag: " + tagParam);
            tag = tagParam;
        }

        // TODO: Seam should use its transaction interceptor for java beans: http://jira.jboss.com/jira/browse/JBSEAM-957
        // and that would allow us to break up this gigantic if/then/else clause easily...
        UserTransaction userTx = null;
        boolean startedTx = false;
        try {
            userTx = (UserTransaction)org.jboss.seam.Component.getInstance("org.jboss.seam.transaction.transaction");
            if (userTx.getStatus() != javax.transaction.Status.STATUS_ACTIVE) {
                startedTx = true;
                userTx.begin();
            }

            Feed feed = null;

            // Find the feed, depending on variations of request parameters
            if (aggregateParam != null && aggregateParam.length() > 0) {

                log.debug("trying to retrieve aggregated feed from cache: " + aggregateParam);

                FeedAggregateCache aggregateCache = (FeedAggregateCache)Component.getInstance(FeedAggregateCache.class);
                List<FeedEntryDTO> result = aggregateCache.get(aggregateParam);
                if (result != null) {
                    feed = new Feed();
                    feed.setAuthor(Messages.instance().get("lacewiki.msg.AutomaticallyGeneratedFeed"));
                    feed.setTitle(Messages.instance().get("lacewiki.msg.AutomaticallyGeneratedFeed") + ": " + aggregateParam);
                    feed.setPublishedDate(new Date());
                    feed.setLink( Preferences.getInstance(WikiPreferences.class).getBaseUrl() );
                    for (FeedEntryDTO feedEntryDTO : result) {
                        feed.getFeedEntries().add(feedEntryDTO.getFeedEntry());
                    }
                }

            } else if (feedIdParam != null && feedIdParam.length() >0) {
                try {

                    log.debug("trying to retrieve feed for id: " + feedIdParam);
                    Long feedId = Long.valueOf(feedIdParam);
                    FeedDAO feedDAO = (FeedDAO)Component.getInstance(FeedDAO.class);
                    feed = feedDAO.findFeed(feedId);
                } catch (NumberFormatException ex) {
                    log.debug("feed identifier couldn't be converted to java.lang.Long");
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed " + feedIdParam);
                }


            } else if (areaNameParam != null && areaNameParam.matches("^[A-Z0-9]+.*")) {
                log.debug("trying to retrieve area: " + areaNameParam);
                WikiNodeDAO nodeDAO = (WikiNodeDAO)Component.getInstance(WikiNodeDAO.class);
                WikiDirectory area = nodeDAO.findAreaUnrestricted(areaNameParam);
                if (area != null && (nodeNameParam == null || !nodeNameParam.matches("^[A-Z0-9]+.*")) && area.getFeed() != null) {
                    log.debug("using feed of area, no node requested: " + area);
                    feed = area.getFeed();
                } else if (area != null && nodeNameParam != null && nodeNameParam.matches("^[A-Z0-9]+.*")) {
                    log.debug("trying to retrieve node: " + nodeNameParam);
                    WikiDirectory nodeDir = nodeDAO.findWikiDirectoryInAreaUnrestricted(area.getAreaNumber(), nodeNameParam);
                    if (nodeDir != null && nodeDir.getFeed() != null) {
                        log.debug("using feed of node: " + nodeDir);
                        feed = nodeDir.getFeed();
                    } else {
                        log.debug("node not found or node has no feed");
                    }
                } else {
                    log.debug("area not found or area has no feed");
                }
            } else {
                log.debug("neither feed id nor area name requested, getting wikiRoot feed");
                WikiNodeFactory factory = (WikiNodeFactory)Component.getInstance(WikiNodeFactory.class);
                feed = factory.loadWikiRoot().getFeed();
            }

            if (feed == null) {
                log.debug("feed not found, returning NOT FOUND");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Feed");
                if (startedTx) userTx.commit();
                return;
            }

            log.debug("checking permissions of " + feed);
            // Authenticate and authorize, first with current user (session) then with basic HTTP authentication
            Integer currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
            if (feed.getReadAccessLevel() > currentAccessLevel) {
                boolean loggedIn = ((Authenticator)Component.getInstance(Authenticator.class)).authenticateBasicHttp(request);
                currentAccessLevel = (Integer)Component.getInstance("currentAccessLevel");
                if (!loggedIn || feed.getReadAccessLevel() > currentAccessLevel) {
                    log.debug("requiring authentication, feed has higher access level than current");
                    response.setHeader("WWW-Authenticate", "Basic realm=\"" + feed.getTitle().replace("\"", "'") + "\"");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                    if (startedTx) userTx.commit();
                    return;
                }
            }

            // TODO: Refactor this mess a little
            log.debug("finally rendering feed");
            SyndFeed syndFeed =
                    createSyndFeed(
                        request.getRequestURL().toString(),
                        syndFeedType,
                        feed,
                        currentAccessLevel,
                        tag,
                        comments,
                        aggregateParam
                    );

            // Write feed to output
            response.setContentType(syndFeedType.contentType);
            response.setCharacterEncoding("UTF-8");
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, response.getWriter());
            response.getWriter().flush();

            log.debug("<<< commit, rendering complete");

            if (startedTx) userTx.commit();
        } catch (Exception ex) {
            try {
                if (startedTx && userTx.getStatus() != javax.transaction.Status.STATUS_MARKED_ROLLBACK) {
                    log.error("error serving feed, setting transaction to rollback only");
                    userTx.setRollbackOnly();
                }
            } catch (Exception rbEx) {
                rbEx.printStackTrace();
            }
            throw new RuntimeException(ex);
        }

        // If the user is not logged in, we might as well destroy the session immediately, saving some memory
        if (request.getSession().isNew() && !Identity.instance().isLoggedIn()) {
            log.debug("destroying session that was only created for reading the feed");
            Session.instance().invalidate();
        }
    }

    public SyndFeed createSyndFeed(String baseURI, SyndFeedType syndFeedType, Feed feed, Integer currentAccessLevel) {
        return createSyndFeed(baseURI, syndFeedType, feed, currentAccessLevel, null, Comments.include, null);
    }

    public SyndFeed createSyndFeed(String baseURI,
                                   SyndFeedType syndFeedType,
                                   Feed feed,
                                   Integer currentAccessLevel,
                                   String tag,
                                   Comments comments,
                                   String aggregateParam) {

        WikiPreferences prefs = Preferences.getInstance(WikiPreferences.class);

        // Create feed
        SyndFeed syndFeed = new SyndFeedImpl();
        String feedUri =
                feed.getId() != null
                    ? "?feedId="+feed.getId()
                    : "?aggregate="+WikiUtil.encodeURL(aggregateParam);
        syndFeed.setUri(baseURI + feedUri);
        syndFeed.setFeedType(syndFeedType.feedType);
        syndFeed.setTitle(prefs.getFeedTitlePrefix() + feed.getTitle());
        if (tag != null) {
            syndFeed.setTitle(
                syndFeed.getTitle() + " - " + Messages.instance().get("lacewiki.label.tagDisplay.Tag") + " '" + tag + "'"
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
            syndEntry.setTitle(entry.getTitlePrefix() + entry.getTitle() + entry.getTitleSuffix());
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
