package org.jboss.seam.wiki.core.ui;

import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.SyndFeedOutput;
import org.jboss.seam.Component;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.model.FeedEntry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.UserTransaction;
import java.io.IOException;
import java.util.*;

public class FeedServlet extends HttpServlet {

    private Map<String,String> feedTypes = new HashMap<String,String>() {{
        put("/atom.seam", "atom_1.0");
        // TODO: Support more of these... also need to consider setContentType() ... put("/rss.seam", "rss_2.0");
    }};

    public FeedServlet() {}

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        String feedId = request.getParameter("feedId");

        if (!feedTypes.containsKey(pathInfo)) return;
        String feedType = feedTypes.get(pathInfo);
        if (feedId == null) return;

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
            Feed feed = feedDAO.findFeed(Long.valueOf(feedId));
            if (feed == null) return;

            // Create feed
            SyndFeed syndFeed = new SyndFeedImpl();
            syndFeed.setFeedType(feedType);
            syndFeed.setTitle(feed.getTitle());
            syndFeed.setLink(request.getRequestURL().toString() + "?feedId=" + feedId);
            syndFeed.setDescription(feed.getDescription());
            syndFeed.setPublishedDate(feed.getPublishedDate());

            // Create feed entries
            List<SyndEntry> syndEntries = new ArrayList<SyndEntry>();
            SortedSet<FeedEntry> entries = feed.getFeedEntries();
            for (FeedEntry entry : entries) {
                SyndEntry syndEntry;
                SyndContent description;
                syndEntry = new SyndEntryImpl();
                syndEntry.setTitle(entry.getTitle());
                syndEntry.setLink(entry.getLink());
                syndEntry.setAuthor(entry.getAuthor());
                syndEntry.setPublishedDate(entry.getPublishedDate());
                syndEntry.setUpdatedDate(entry.getUpdatedDate());
                description = new SyndContentImpl();
                description.setType(entry.getDescriptionType());
                description.setValue(entry.getDescriptionValue());
                syndEntry.setDescription(description);
                syndEntries.add(syndEntry);
            }
            syndFeed.setEntries(syndEntries);

            // Write feed to output
            response.setContentType("application/atom+xml"); // TODO: Make this more flexible
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(syndFeed, response.getWriter());
            response.getWriter().flush();

            if (startedTx) userTx.commit();
        } catch (Exception ex) {
            try {
                if (startedTx) userTx.rollback();
            } catch (Exception rbEx) {
                rbEx.printStackTrace();
            }
            throw new RuntimeException(ex);
        }
    }

}
