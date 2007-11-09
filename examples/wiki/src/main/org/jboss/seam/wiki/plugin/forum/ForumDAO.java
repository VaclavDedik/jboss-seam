package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Comment;
import org.hibernate.Session;
import org.hibernate.ScrollableResults;
import org.hibernate.transform.ResultTransformer;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@Name("forumDAO")
@AutoCreate
public class ForumDAO {

    @In
    EntityManager entityManager;

    @In
    EntityManager restrictedEntityManager;

    public List<Directory> findForums(Directory forumDirectory) {
        return getSession(true).getNamedQuery("forums")
                .setParameter("parentDir", forumDirectory)
                .list();
    }
    
    public Map<Long, ForumInfo> findForumInfo(Directory forumDirectory) {
        final Map<Long, ForumInfo> forumInfoMap = new HashMap<Long, ForumInfo>();

        // Append thread and posting count
        getSession(true).getNamedQuery("forumTopicPostCount")
            .setParameter("parentDir", forumDirectory)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        forumInfoMap.put(
                            (Long) result[0],
                            new ForumInfo( (Long)result[1], (Long)result[2])
                        );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Append last topic Document
        getSession(true).getNamedQuery("forumLastTopic")
            .setParameter("parentDir", forumDirectory)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0]))
                            forumInfoMap.get( (Long)result[0] ).setLastTopic( (ForumTopic)result[1] );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        // Append last reply Comment
        getSession(true).getNamedQuery("forumLastComment")
            .setParameter("nsLeft", forumDirectory.getNsLeft())
            .setParameter("nsRight", forumDirectory.getNsRight())
            .setParameter("nsThread", forumDirectory.getNsThread())
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        if (forumInfoMap.containsKey((Long)result[0]))
                            forumInfoMap.get( (Long)result[0] ).setLastComment( (Comment)result[1] );
                        return null;
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .list();

        return forumInfoMap;
    }

    public Long findTopicCount(Directory forum) {
        ScrollableResults cursor =
            getSession(true).getNamedQuery("forumTopics")
                .setParameter("forum", forum)
                .scroll();
        cursor.last();
        Long count = cursor.getRowNumber() + 1l;
        cursor.close();

        return count;
    }
   
    public List<ForumTopic> findTopics(Directory forum, long firstResult, long maxResults) {
        return getSession(true).getNamedQuery("forumTopics")
            .setParameter("forum", forum)
            .setResultTransformer(
                new ResultTransformer() {
                    public Object transformTuple(Object[] result, String[] strings) {
                        return ForumTopic.fromArray(result);
                    }
                    public List transformList(List list) { return list; }
                }
            )
            .setFirstResult(new Long(firstResult).intValue())
            .setMaxResults(new Long(maxResults).intValue())
            .list();
    }

    public List<ForumTopic> findUnreadTopics(Date lastLoginDate) {
        return getSession(true).getNamedQuery("forumUnreadTopics")
                .setParameter("lastLoginDate", lastLoginDate)
                .list();
    }

    private Session getSession(boolean restricted) {
        if (restricted) {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
        } else {
            return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) entityManager).getDelegate());
        }
    }
}
