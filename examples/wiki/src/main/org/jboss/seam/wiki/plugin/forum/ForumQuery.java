package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;

import java.util.*;
import java.io.Serializable;

@Name("forumQuery")
@Scope(ScopeType.CONVERSATION)
public class ForumQuery implements Serializable {

    public static final String TOPIC_PAGE = "topicPage";

    @Logger
    Log log;

    private Pager pager;

    @In("#{preferences.get('Forum')}")
    ForumPreferences prefs;

    @RequestParameter
    public void setPage(Integer page) {
        if (pager == null) pager = new Pager(prefs.getTopicsPerPage());
        pager.setPage(page);
        Contexts.getSessionContext().set(TOPIC_PAGE, page);
    }

    public Pager getPager() {
        return pager;
    }

    @In
    WikiDirectory currentDirectory;

    @In
    User currentUser;

    @In
    int currentAccessLevel;

    @In
    ForumDAO forumDAO;

    /* ####################### FORUMS ########################## */

    List<ForumInfo> forums;
    public List<ForumInfo> getForums() {
        if (forums == null) loadForums();
        return forums;
    }

    @Observer(value = {"Forum.forumListRefresh", "PersistenceContext.filterReset"}, create = false)
    public void loadForums() {

        Map<Long, ForumInfo> forumInfo = forumDAO.findForums(currentDirectory);

        // Find unread postings
        if (!currentUser.isAdmin() && !currentUser.isGuest()) {
            log.debug("finding unread topics since: " + currentUser.getPreviousLastLoginOn());

            Map<Long,Long> unreadTopicsWithParent =
                    forumDAO.findUnreadTopicAndParentIds(currentDirectory, currentUser.getPreviousLastLoginOn());

            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance("forumTopicReadManager");

            for (Map.Entry<Long, Long> unreadTopicAndParent: unreadTopicsWithParent.entrySet()) {
                if (forumInfo.containsKey(unreadTopicAndParent.getValue()) &&
                    !forumTopicReadManager.isTopicIdRead(unreadTopicAndParent.getValue(), unreadTopicAndParent.getKey()) ) {
                    forumInfo.get(unreadTopicAndParent.getValue()).setUnreadPostings(true);
                }
            }
        }
        forums = new ArrayList<ForumInfo>();
        forums.addAll(forumInfo.values());
    }

    /* ####################### TOPICS ########################## */

    private List<TopicInfo> topics;

    public List<TopicInfo> getTopics() {
        if (topics == null) loadTopics();
        return topics;
    }

    @Observer(value = {"Forum.topicListRefresh", "PersistenceContext.filterReset"}, create = false)
    public void loadTopics() {
        log.debug("loading forum topics");
        pager.setNumOfRecords( forumDAO.findTopicCount(currentDirectory) );

        if (pager.getNumOfRecords() == 0) {
            topics = Collections.emptyList();
            return;
        }

        Map<Long, TopicInfo> topicInfo = forumDAO.findTopics(currentDirectory, pager.getNextRecord(), pager.getPageSize());

        if (!currentUser.isAdmin() && !currentUser.isGuest()) {
            log.debug("finding unread topics since: " + currentUser.getPreviousLastLoginOn());

            Map<Long,Long> unreadTopicsWithParent =
                    forumDAO.findUnreadTopicAndParentIdsInForum(currentDirectory, currentUser.getPreviousLastLoginOn());

            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance("forumTopicReadManager");

            for (Map.Entry<Long, TopicInfo> topicInfoEntry: topicInfo.entrySet()) {
                topicInfoEntry.getValue().setUnread(
                    unreadTopicsWithParent.containsKey(topicInfoEntry.getKey()) &&
                    !forumTopicReadManager.isTopicIdRead(
                        unreadTopicsWithParent.get(topicInfoEntry.getKey()),
                        topicInfoEntry.getKey()
                    )
                );
            }
        }

        topics = new ArrayList<TopicInfo>();
        topics.addAll(topicInfo.values());
    }

}
