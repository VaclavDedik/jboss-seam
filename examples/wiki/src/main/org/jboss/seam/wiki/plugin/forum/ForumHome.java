package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.ScopeType;
import org.jboss.seam.international.Messages;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.action.DirectoryHome;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import java.util.List;

@Name("forumHome")
@Scope(ScopeType.CONVERSATION)
public class ForumHome extends DirectoryHome {

    @In
    ForumDAO forumDAO;

    @In
    WikiDirectory currentDirectory;

    private boolean showForm = false;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected boolean isPageRootController() {
        return false;
    }

    @Override
    public Class<WikiDirectory> getEntityClass() {
        return WikiDirectory.class;
    }

    @Override
    public void create() {
        super.create();
        setParentNodeId(currentDirectory.getId());
    }

    @Override
    public WikiDirectory afterNodeCreated(WikiDirectory node) {
        node.setWriteProtected(true); // Only allow admins to edit it
        setHasFeed(true); // New forum always has a feed
        return super.afterNodeCreated(node);
    }

    @Override
    public String persist() {
        // This is _always_ a subdirectory in an area
        getInstance().setAreaNumber(getParentNode().getAreaNumber());

        String outcome = super.persist();
        if (outcome != null) {

            // Create a mandatory menu item
            List<WikiMenuItem> menuItems = forumDAO.findForumsMenuItems(getParentNode());
            WikiMenuItem newMenuItem = new WikiMenuItem(getInstance());
            menuItems.add(newMenuItem);
            for (WikiMenuItem menuItem : menuItems) {
                menuItem.setDisplayPosition(menuItems.indexOf(menuItem));
            }
            getEntityManager().persist(newMenuItem);

            // Default document is topic list
            WikiDocumentDefaults topicListDefaults =
                new WikiDocumentDefaults() {
                    @Override
                    public String getName() {
                        return getInstance().getName() + " " + Messages.instance().get("forum.label.Forum");
                    }
                    @Override
                    public String[] getHeaderMacrosAsString() {
                        return new String[] {"clearBackground", "hideControls", "hideComments", "hideTags", "hideCreatorHistory"};
                    }
                    @Override
                    public String[] getContentMacrosAsString() {
                        return new String[] {"forumTopics"};
                    }
                    @Override
                    public String getContentText() {
                        return "";
                    }
                    @Override
                    public void setOptions(WikiDocument document) {
                        document.setAreaNumber(getInstance().getAreaNumber());
                        document.setWikiname(WikiUtil.convertToWikiName(document.getName()));
                        document.setNameAsTitle(true);
                        document.setReadAccessLevel(getInstance().getReadAccessLevel());
                        document.setWriteAccessLevel(org.jboss.seam.wiki.core.model.Role.ADMINROLE_ACCESSLEVEL);
                        document.setEnableComments(false);
                        document.setEnableCommentForm(false);
                        document.setEnableCommentsOnFeeds(false);
                        document.setCreatedBy(getCurrentUser());
                    }
                };

            WikiDocument topicList = new WikiDocument(topicListDefaults);

            topicList.setParent(getInstance());
            getInstance().setDefaultFile(topicList);

            getEntityManager().persist(topicList);
            getEntityManager().flush();

            endConversation();
        }
        return null; // Prevent navigation
    }

    @Override
    public String update() {
        String outcome = super.update();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    @Override
    public String remove() {
        String outcome = super.remove();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

/* -------------------------- Messages ------------------------------ */

    @Override
    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Persist",
                "Forum '{0}' has been saved.",
                getInstance().getName()
        );
    }

    @Override
    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Update",
                "Forum '{0}' has been updated.",
                getInstance().getName()
        );
    }

    @Override
    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "forum.msg.Forum.Delete",
                "Forum '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    @Override
    protected void feedCreatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "forum.msg.Feed.Create",
                "Created syndication feed for this forum");
    }

    @Override
    protected void feedRemovedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "forum.msg.Feed.Remove",
                "Removed syndication feed of this forum");
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the forum list refresh
        Events.instance().raiseEvent("Forum.forumListRefresh");
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }


    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void newForum() {
        initEditor();
        showForm = true;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void edit(Long forumId) {
        setId(forumId);
        initEditor();
        showForm = true;
    }

    public void cancel() {
        endConversation();
    }

}
