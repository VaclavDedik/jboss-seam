/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.renderer;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Feed;
import org.jboss.seam.wiki.core.ui.FeedServlet;

import java.io.Serializable;

/**
 * Renders outgoing URLs in a unified fashion, see urlrewrite.xml for incoming URL GET request rewriting.
 * <p>
 * Note that some of the rendering is delegated into the domain model for subclasses of <tt>Node</tt>.
 * </p>
 *
 * @author Christian Bauer
 */

@Name("wikiURLRenderer")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiURLRenderer implements Serializable {

    @In
    String basePath;

    @In("#{preferences.get('Wiki')}")
    WikiPreferences prefs;

    public String renderSearchURL(String search) {
        if (search == null || search.length() == 0) return "";
        StringBuilder url = new StringBuilder();
        String skin = Component.getInstance("skin") != null ? (String)Component.getInstance("skin") : "d";
        url.append(basePath).append("/search_").append(skin).append(".seam?query=").append(encodeURL(search));
        return url.toString();
    }

    public String renderTagURL(String tag) {
        if (tag == null || tag.length() == 0) return "";
        StringBuilder url = new StringBuilder();
        url.append(basePath).append("/tag/").append(encodeURL(tag));
        return url.toString();
    }

    public String renderUserInfoURL(User user) {
        if (user == null || user.getUsername() == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(basePath).append("/user/").append(user.getUsername());
        return url.toString();
    }

    public String renderAggregateFeedURL(String aggregateId) {
        if (aggregateId == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(basePath)
            .append("/service/Feed/atom/Aggregate/")
            .append(aggregateId);
        return url.toString();
    }

    public String renderFeedURL(Feed feed, String tag, String comments) {
        if (feed == null || feed.getId() == null) return "";
        StringBuilder url = new StringBuilder();
        url.append(basePath).append("/service/Feed/atom").append(feed.getURL());
        if (comments != null && comments.length() >0) {
            url.append("/Comments/").append(FeedServlet.Comments.valueOf(comments));
        }
        if (tag != null && tag.length() >0) url.append("/Tag/").append(encodeURL(tag));
        return url.toString();
    }

    public String renderURL(WikiNode node) {
        if (node == null || node.getId() == null) return "";
        return prefs.isRenderPermlinks() ? renderPermURL(node) : renderWikiURL(node);
    }

    public String renderPermURL(WikiNode node) {
        if (node == null || node.getId() == null) return "";
        return basePath + "/" + node.getPermURL(prefs.getPermlinkSuffix());
    }

    public String renderWikiURL(WikiNode node) {
        if (node == null || node.getId() == null) return "";
        return basePath + "/" + node.getWikiURL();
    }

    private String encodeURL(String s) {
        return WikiUtil.encodeURL(s);
    }

}
