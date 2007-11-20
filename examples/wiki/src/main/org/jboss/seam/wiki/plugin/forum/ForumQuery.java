package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.Serializable;

@Name("forumQuery")
@Scope(ScopeType.CONVERSATION)
public class ForumQuery implements Serializable {

    private Pager pager;

    @In
    ForumPreferences forumPreferences;

    @RequestParameter
    public void setPage(Integer page) {
        if (pager == null) pager = new Pager(forumPreferences.getTopicsPerPage());
        pager.setPage(page);
    }

    public Pager getPager() {
        return pager;
    }

    @In
    Directory currentDirectory;

    @In
    User currentUser;

    @In
    int currentAccessLevel;

    @In
    ForumDAO forumDAO;

    /* ####################### FORUMS ########################## */

    List<Directory> forums;
    public List<Directory> getForums() {
        if (forums == null) loadForums();
        return forums;
    }

    Map<Long, ForumInfo> forumInfo;
    public Map<Long, ForumInfo> getForumInfo() {
        return forumInfo;
    }

    @Observer(value = {"Forum.forumListRefresh", "PersistenceContext.filterReset"}, create = false)
    public void loadForums() {

        forums = forumDAO.findForums(currentDirectory);
        forumInfo = forumDAO.findForumInfo(currentDirectory);

        // Find unread postings
        User adminUser = (User)Component.getInstance("adminUser");
        User guestUser = (User)Component.getInstance("guestUser");
        if ( !(currentUser.getId().equals(guestUser.getId())) &&
             !(currentUser.getId().equals(adminUser.getId())) ) {
            List<ForumTopic> unreadTopics = forumDAO.findUnreadTopics(currentUser.getPreviousLastLoginOn());
            ForumCookie forumCookie = (ForumCookie)Component.getInstance("forumCookie");
            for (ForumTopic unreadTopic : unreadTopics) {
                if (forumInfo.containsKey(unreadTopic.getParent().getId()) &&
                    !forumCookie.getCookieValues().containsKey(unreadTopic.getId().toString())) {
                    forumInfo.get(unreadTopic.getParent().getId()).setUnreadPostings(true);
                }
            }
        }
    }

    /* ####################### TOPICS ########################## */

    private List<ForumTopic> topics;

    public List<ForumTopic> getTopics() {
        if (topics == null) loadTopics();
        return topics;
    }

    @Observer(value = {"Forum.topicPersisted", "PersistenceContext.filterReset"}, create = false)
    public void loadTopics() {
        pager.setNumOfRecords( forumDAO.findTopicCount(currentDirectory) );
        topics = pager.getNumOfRecords() > 0
            ? forumDAO.findTopics(currentDirectory, pager.getNextRecord(), pager.getPageSize())
            : new ArrayList<ForumTopic>();

        User adminUser = (User)Component.getInstance("adminUser");
        User guestUser = (User)Component.getInstance("guestUser");
        // Find unread postings
        if ( !(currentUser.getId().equals(guestUser.getId())) &&
             !(currentUser.getId().equals(adminUser.getId())) ) {
            List<ForumTopic> unreadTopics = forumDAO.findUnreadTopics(currentUser.getPreviousLastLoginOn());
            ForumCookie forumCookie = (ForumCookie)Component.getInstance("forumCookie");
            // TODO: This is nested interation but it's difficult to make this more efficient
            for (ForumTopic topic : topics) {
                for (ForumTopic unreadTopic : unreadTopics) {
                    if (unreadTopic.getId().equals(topic.getId())&&
                        !forumCookie.getCookieValues().containsKey(topic.getId().toString())) {
                        topic.setUnread(true);
                    }
                }
            }
        }
    }

}
