package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.Component;
import org.hibernate.Session;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import java.util.*;

@Name("forumDAO")
@AutoCreate
public class ForumDAO {

    @In
    EntityManager entityManager;

    @In
    EntityManager restrictedEntityManager;

    @In
    Integer currentAccessLevel;

    public List<WikiMenuItem> findForumsMenuItems(WikiDirectory forumsDirectory) {
        return getSession(true).getNamedQuery("forumsMenuItems")
                .setParameter("parentDir", forumsDirectory)
                .list();
    }

    public Map<Long, ForumInfo> findForums(WikiDirectory forumsDirectory) {
        final Map<Long, ForumInfo> forumInfoMap = new LinkedHashMap<Long, ForumInfo>();

        getSession(true).getNamedQuery("forums")
            .setParameter("parentDir", forumsDirectory)
            .setComment("Finding all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        forumInfoMap.put(
                            (Long) result[0],
                            new ForumInfo( (WikiDirectory)result[1])
                        );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Find topic count (topics are just wiki documents in the forum directories)
        getSession(true).getNamedQuery("forumTopicCount")
            .setParameter("parentDir", forumsDirectory)
            .setComment("Finding topic count for all forums")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0])) {
                            ForumInfo info = forumInfoMap.get( (Long)result[0] );
                            info.setTotalNumOfTopics((Long)result[1]);
                            info.setTotalNumOfPosts(info.getTotalNumOfTopics());
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Add reply count to topic count to get total num of posts
        getSession(true).getNamedQuery("forumReplyCount")
            .setParameter("parentDirId", forumsDirectory.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Finding reply count for all forums")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0])) {
                            ForumInfo info = forumInfoMap.get( (Long)result[0] );
                            info.setTotalNumOfPosts(
                                info.getTotalNumOfPosts() + (Long)result[1]
                            );
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Append last topic WikiDocument
        getSession(true).getNamedQuery("forumLastTopic")
            .setParameter("parentDir", forumsDirectory)
            .setComment("Finding last topics for all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0]))
                            forumInfoMap.get( (Long)result[0] ).setLastTopic( (WikiDocument)result[1] );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Append last reply WikiComment
        getSession(true).getNamedQuery("forumLastReply")
            .setParameter("parentDirId", forumsDirectory.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Finding last replies for all forums")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0]))
                            forumInfoMap.get( (Long)result[0] ).setLastComment( (WikiComment)result[1] );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        return forumInfoMap;
    }

    public Map<Long, Long> findUnreadTopicAndParentIds(WikiDirectory forumsDir, Date lastLoginDate) {
        return findUnreadTopicAndParentIds("forumUnreadTopics", "forumUnreadReplies", forumsDir, lastLoginDate);
    }

    public Map<Long, Long> findUnreadTopicAndParentIdsInForum(WikiDirectory forum, Date lastLoginDate) {
        return findUnreadTopicAndParentIds("forumUnreadTopicsInForum", "forumUnreadRepliesInForum", forum, lastLoginDate);
    }

    private Map<Long, Long> findUnreadTopicAndParentIds(String unreadTopicsQuery, String unreadRepliesQuery,
                                                        WikiDirectory directory, Date lastLoginDate) {
        final Map<Long, Long> unreadTopics = new HashMap<Long, Long>();

        getSession(true).getNamedQuery(unreadTopicsQuery)
            .setParameter("parentDir", directory)
            .setParameter("lastLoginDate", lastLoginDate)
            .setComment("Finding unread topics")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] objects, String[] strings) {
                        unreadTopics.put((Long)objects[0], (Long)objects[1]);
                        return null;
                    }
                    public List transformList(List list) { return list;}
                }
            )
            .list();

        getSession(true).getNamedQuery(unreadRepliesQuery)
            .setParameter("parentDir", directory)
            .setParameter("lastLoginDate", lastLoginDate)
            .setComment("Finding unread replies")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] objects, String[] strings) {
                        unreadTopics.put((Long)objects[0], (Long)objects[1]);
                        return null;
                    }
                    public List transformList(List list) { return list;}
                }
            )
            .list();

        return unreadTopics;
    }

    public Long findTopicCount(WikiDirectory forum) {
        return (Long)getSession(true).getNamedQuery("forumTopicsCount")
                .setParameter("parentDir", forum)
                .setComment("Retrieving forum topics count")
                .setCacheable(true)
                .uniqueResult();
    }

    public Map<Long, TopicInfo> findTopics(WikiDirectory forum, long firstResult, long maxResults) {
        final Map<Long, TopicInfo> topicInfoMap = new LinkedHashMap<Long, TopicInfo>();

        getSession(true).getNamedQuery("forumTopics")
            .setParameter("parentNodeId", forum.getId())
            .setParameter("readAccessLevel", currentAccessLevel)
            .setComment("Retrieving forum topics")
            .setFirstResult(new Long(firstResult).intValue())
            .setMaxResults(new Long(maxResults).intValue())
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        topicInfoMap.put(
                            ((WikiDocument)result[0]).getId(),
                            new TopicInfo( (WikiDocument)result[0], (Integer)result[1], (Boolean)result[2])
                        );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        List<Long> topicIdsWithReplies = new ArrayList<Long>();
        for (Map.Entry<Long, TopicInfo> entry : topicInfoMap.entrySet()) {
            if (entry.getValue().isReplies()) topicIdsWithReplies.add(entry.getKey());
        }
        if (topicIdsWithReplies.size() == 0) return topicInfoMap; // Early exit possible
        
        getSession(true).getNamedQuery("forumTopicsReplies")
            .setParameterList("topicIds", topicIdsWithReplies)
            .setComment("Retrieving forum topic replies")
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (topicInfoMap.containsKey((Long)result[0])) {
                            TopicInfo info = topicInfoMap.get( (Long)result[0] );
                            info.setNumOfReplies((Long)result[1]);
                            info.setLastComment((WikiComment)result[2]);
                        }
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        return topicInfoMap;
    }

    public List<User> findPostersAndRatingPoints(Long forumId, int maxResults, List<String> excludeRoles) {

        if (excludeRoles.size() == 0) {
            excludeRoles.add("guest"); // By default, don't show guests, query requires _some_ exclude
        }

        final List<User> postersAndRatingPoints = new ArrayList<User>();

        getSession(true).getNamedQuery("forumPostersAndRatingPoints")
            .setParameter("parentDirId", forumId)
            .setParameterList("ignoreUserInRoles", excludeRoles )
            .setMaxResults(maxResults)
            .setComment("Retrieving forum posters and rating points")
            .setCacheable(true)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        ((User)result[0]).setRatingPoints((Long)result[1]);
                        postersAndRatingPoints.add((User)result[0]);
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();
        return postersAndRatingPoints;
    }

    private Session getSession(boolean restricted) {
        if (restricted) {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
        } else {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) entityManager).getDelegate());
        }
    }
}
