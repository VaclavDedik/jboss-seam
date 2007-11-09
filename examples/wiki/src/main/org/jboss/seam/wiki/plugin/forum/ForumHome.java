package org.jboss.seam.wiki.plugin.forum;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import javax.faces.application.FacesMessage;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.action.NodeHome;
import org.jboss.seam.wiki.core.dao.FeedDAO;
import org.jboss.seam.wiki.util.WikiUtil;

import java.util.Date;

@Name("forumHome")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class ForumHome extends NodeHome<Directory> {

    @In
    Directory currentDirectory;

    @In
    FeedDAO feedDAO;

    private boolean showForm = false;
    private boolean hasFeed = true;

    /* -------------------------- Basic Overrides ------------------------------ */

    protected Directory createInstance() {
        Directory forum = super.createInstance();
        forum.setName("New Forum");
        hasFeed = true;
        return forum;
    }

    public Directory find() {
        Directory forum = super.find();
        hasFeed = forum.getFeed()!=null;
        return forum;
    }

    public void create() {
        super.create();
        setParentDirectoryId(currentDirectory.getId());
        init();
    }

    protected boolean beforePersist() {
        createOrRemoveFeed();
        return super.beforePersist();
    }

    public String persist() {
        // This is _always_ a subdirectory in an area
        getInstance().setAreaNumber(getParentDirectory().getAreaNumber());

        String outcome = super.persist();
        if (outcome != null) {

            // Default document is topic list
            Document topicList = createDefaultDocument();
            topicList.setAreaNumber(getInstance().getAreaNumber());
            topicList.setName(getInstance().getName() + " Forum");
            topicList.setWikiname(WikiUtil.convertToWikiName(topicList.getName()));
            topicList.setCreatedBy(getCurrentUser());
            topicList.setLastModifiedBy(getCurrentUser());
            topicList.setLastModifiedOn(new Date());

            getInstance().addChild(topicList);
            getInstance().setDefaultDocument(topicList);

            getEntityManager().persist(topicList);
            getEntityManager().flush();

            endConversation();
        }
        return null; // Prevent navigation
    }

    protected boolean beforeUpdate() {
        createOrRemoveFeed();
        return super.beforeUpdate();
    }

    public String update() {
        String outcome = super.update();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    protected boolean beforeRemove() {
        // Remove all children (nested, recursively, udpates the second-level cache)
        getNodeDAO().removeChildren(getInstance());

        return true;
    }

    public String remove() {
        String outcome = super.remove();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Persist",
                "Forum '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Update",
                "Forum '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Delete",
                "Forum '{0}' has been deleted.",
                getInstance().getName()
        );
    }
    
    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the forum list refresh
        Events.instance().raiseEvent("Forum.forumListRefresh");
    }

    private Document createDefaultDocument() {
        Document doc = new Document();
        doc.setNameAsTitle(true);
        doc.setReadAccessLevel(getInstance().getReadAccessLevel());
        doc.setWriteAccessLevel(getInstance().getWriteAccessLevel());
        doc.setEnableComments(false);
        doc.setEnableCommentForm(false);

        String[] defaultMacros = {"clearBackground", "hideControls", "hideComments", "hideTags", "hideCreatorHistory", "forumTopics"};

        StringBuilder contentWithMacros = new StringBuilder();
        StringBuilder macros = new StringBuilder();
        for (String s : defaultMacros) {
            contentWithMacros.append("[<=").append(s).append("]\n");
            macros.append(s).append(" ");
        }
        doc.setContent(contentWithMacros.toString());
        doc.setMacros(macros.substring(0, macros.length()-1));

        return doc;
    }

    public void createOrRemoveFeed() {
        if (hasFeed && getInstance().getFeed() == null) {
            // Does not have a feed but user wants one, create it
            feedDAO.createFeed(getInstance());

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "forum.msg.Feed.Create",
                "Created syndication feed for this forum");

        } else if (!hasFeed && getInstance().getFeed() != null) {
            // Does have feed but user doesn't want it anymore... delete it
            feedDAO.removeFeed(getInstance());

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "forum.msg.Feed.Remove",
                "Removed syndication feed of this forum");

        } else if (getInstance().getFeed() != null) {
            // Does have a feed and user still wants it, update the feed
            feedDAO.updateFeed(getInstance());
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    public boolean isHasFeed() {
        return hasFeed;
    }

    public void setHasFeed(boolean hasFeed) {
        this.hasFeed = hasFeed;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void newForum() {
        init();
        showForm = true;
    }

    @Begin(flushMode = FlushModeType.MANUAL)
    public void edit(Long forumId) {
        setId(forumId);
        init();
        showForm = true;
    }

    public void cancel() {
        endConversation();
    }

    public void resetFeed() {
        if (getInstance().getFeed() != null) {
            getLog().debug("resetting feed of directory");
            getInstance().getFeed().getFeedEntries().clear();
            getInstance().getFeed().setPublishedDate(new Date());
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "forum.msg.Feed.Reset",
                "Queued removal of all feed entries from the syndication feed of this directory, please update to finalize");
        }
    }

}
