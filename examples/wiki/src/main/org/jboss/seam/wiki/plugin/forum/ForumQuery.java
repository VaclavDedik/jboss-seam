package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.WikiDirectory;

import java.io.Serializable;
import java.util.*;

@Name("forumQuery")
@Scope(ScopeType.CONVERSATION)
public class ForumQuery implements Serializable {

    public static final String TOPIC_PAGE = "topicPage";

    @Logger
    Log log;

    private Pager pager;

    @In("#{preferences.get('Forum')}")
    ForumPreferences forumPrefs;

    @In("#{preferences.get('ForumTopPosters', currentMacro)}")
    ForumTopPostersPreferences forumTopPostersPrefs;

    @RequestParameter
    public void setPage(Integer page) {
        if (pager == null) pager = new Pager(forumPrefs.getTopicsPerPage());
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

    @In(create = true)
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

            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance(ForumTopicReadManager.class);

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

            ForumTopicReadManager forumTopicReadManager = (ForumTopicReadManager)Component.getInstance(ForumTopicReadManager.class);

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

    /* ####################### POSTERS ########################## */

    private Map<Long, List<User>> forumsTopPosters = new HashMap<Long, List<User>>();

    public List<User> getTopPosters() {
        Long forumId = resolveForumId(forumTopPostersPrefs.getForumLink());
        return forumId != null ? forumsTopPosters.get(forumId) : null;
    }

    @Observer(value = "Macro.render.forumTopPosters", create = true)
    public void loadForumTopPosters() {
        log.debug("loading top posters of forum: " + forumTopPostersPrefs.getForumLink());

        Long forumId = resolveForumId(forumTopPostersPrefs.getForumLink());
        if (forumId == null) {
            log.debug("could not resolve forum id for forum start page link: " + forumTopPostersPrefs.getForumLink());
            return;
        }

        log.debug("resolved forum id: " + forumId);

        List<String> excludeRoles = new ArrayList<String>();
        if (forumTopPostersPrefs.getExcludeRoles() != null &&
            forumTopPostersPrefs.getExcludeRoles().length() > 0) {
            log.debug("excluding posters with roles: " + forumTopPostersPrefs.getExcludeRoles());
            excludeRoles = Arrays.asList(forumTopPostersPrefs.getExcludeRoles().split(" "));
        }

        log.debug("loading top " + forumTopPostersPrefs.getNumberOfPosters() + " posters of forum id: " + forumId);
        List<User> topPosters =
            forumDAO.findPostersAndRatingPoints(
                forumId,
                forumTopPostersPrefs.getNumberOfPosters().intValue(),
                excludeRoles
            );
        log.debug("found top posters: " + topPosters.size());
        forumsTopPosters.put(forumId, topPosters);
    }

    private Long resolveForumId(String forumLink) {
        if (forumLink == null || forumLink.length() == 0) return null;
        WikiLinkResolver resolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        Map<String, WikiLink> resolvedLinks = new HashMap<String, WikiLink>();
        resolver.resolveLinkText(currentDirectory.getAreaNumber(), resolvedLinks, forumLink);
        WikiLink resolvedLink = resolvedLinks.get(forumLink);
        if (resolvedLink.isBroken() || resolvedLink.getFile().getId() == null) {
            return null;
        } else {
            // Parent of forum start page is the forum directory
            return resolvedLink.getFile().getParent().getId();
        }
    }


}
