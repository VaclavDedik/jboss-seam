package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;
import org.hibernate.transform.AliasToBeanResultTransformer;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.ArrayList;

@Name("blogDAO")
@AutoCreate
public class BlogDAO {

    @In
    protected EntityManager restrictedEntityManager;

    public List<BlogEntry> findBlogEntriesWithCommentCount(WikiDirectory startDir,
                                                           WikiDocument ignoreDoc,
                                                           String orderByProperty,
                                                           boolean orderDescending,
                                                           long firstResult,
                                                           long maxResults,
                                                           Integer year,
                                                           Integer month,
                                                           Integer day,
                                                           final String tag) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select d as entryDocument").append(", ");
        queryString.append("(select count(c) from WikiComment c where c.file  = d) as commentCount").append(" ");

        queryString.append("from WikiDocument d fetch all properties where d.parent.id in").append(" ");
        queryString.append("(");
        queryString.append("select d1.id from ").append(startDir.getTreeSuperclassEntityName()).append(" d1, ");
        queryString.append(startDir.getTreeSuperclassEntityName()).append(" d2 ");
        queryString.append("where d1.nsThread = :thread and d2.nsThread = :thread").append(" ");
        queryString.append("and d1.nsLeft between d2.nsLeft and d2.nsRight").append(" ");
        queryString.append("and d2.nsLeft >= :startLeft and d2.nsRight <= :startRight").append(" ");
        queryString.append(")");

        if (ignoreDoc != null && ignoreDoc.getId() != null)
            queryString.append("and not d = :ignoreDoc").append(" ");

        queryString.append("and not d.macros like '%blogDirectory%'").append(" ");
        queryString.append("and not d.macros like '%feedTeasers%'").append(" ");

        if (year != null) queryString.append("and year(d.createdOn) = :limitYear").append(" ");
        if (month != null) queryString.append("and month(d.createdOn) = :limitMonth").append(" ");
        if (day != null) queryString.append("and day(d.createdOn) = :limitDay").append(" ");
        if (tag != null && tag.length()>0) queryString.append("and d.tags like :tag").append(" ");

        queryString.append("order by d.").append(orderByProperty).append(" ");
        queryString.append( orderDescending ? "desc" : "asc").append("");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", startDir.getNsThread());
        nestedSetQuery.setParameter("startLeft", startDir.getNsLeft());
        nestedSetQuery.setParameter("startRight", startDir.getNsRight());
        if (ignoreDoc != null && ignoreDoc.getId() != null)
            nestedSetQuery.setParameter("ignoreDoc", ignoreDoc);

        if (year != null) nestedSetQuery.setParameter("limitYear", year);
        if (month != null) nestedSetQuery.setParameter("limitMonth", month);
        if (day != null) nestedSetQuery.setParameter("limitDay", day);
        if (tag != null && tag.length()>0) nestedSetQuery.setParameter("tag", "%" + tag + "%");

        nestedSetQuery.setFirstResult( new Long(firstResult).intValue() );
        nestedSetQuery.setMaxResults( new Long(maxResults).intValue() );
        nestedSetQuery.setResultTransformer(
            new AliasToBeanResultTransformer(BlogEntry.class) {
                public Object transformTuple(Object[] result, String[] aliases) {
                    WikiDocument doc = (WikiDocument)result[0];
                    if (tag == null || doc.isTagged(tag)) {
                        return super.transformTuple(result, aliases);
                    }
                    return null;
                }
                public List transformList(List list) {
                    List listWithoutNulls = new ArrayList();
                    for (Object o : super.transformList(list)) if (o != null) listWithoutNulls.add(o);
                    return listWithoutNulls;
                }
            }
        );

        return (List<BlogEntry>)nestedSetQuery.list();
    }

    public Long countBlogEntries(WikiNode startNode, WikiNode ignoreNode, Integer year, Integer month, Integer day, String tag) {
        return countBlogEntries(startNode, ignoreNode, false, false, false, year, month, day, tag).get(0).getNumOfEntries();
    }

    public List<BlogEntryCount> countAllBlogEntriesGroupByYearMonth(WikiNode startNode, WikiNode ignoreNode, String tag) {
        return countBlogEntries(startNode, ignoreNode, true, true, false, null, null, null, tag);
    }

    private List<BlogEntryCount> countBlogEntries(WikiNode startNode, WikiNode ignoreNode,
                                                 boolean projectYear, boolean projectMonth, boolean projectDay,
                                                 Integer limitYear, Integer limitMonth, Integer limitDay,
                                                 String tag) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select count(n1.id) as numOfEntries");

        if (projectYear) queryString.append(", ").append("year(n1.createdOn) as year");
        if (projectMonth) queryString.append(", ").append("month(n1.createdOn) as month");
        if (projectDay) queryString.append(", ").append("day(n1.createdOn) as day");

        queryString.append(" ");
        /*
        queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");
        */
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        queryString.append("and n2.class = :clazz").append(" ");
        queryString.append("and not n1.macros like '%blogDirectory%'").append(" ");

        if (ignoreNode.getId() != null)
            queryString.append("and not n1 = :ignoreNode").append(" ");

        if (limitYear != null) queryString.append("and year(n1.createdOn) = :limitYear").append(" ");
        if (limitMonth!= null) queryString.append("and month(n1.createdOn) = :limitMonth").append(" ");
        if (limitDay != null) queryString.append("and day(n1.createdOn) = :limitDay").append(" ");

        if (tag != null && tag.length()>0) queryString.append("and n1.tags like :tag").append(" ");

        if (projectYear || projectMonth || projectDay)  queryString.append("group by").append(" ");
        if (projectYear)    queryString.append("year(n1.createdOn)");
        if (projectMonth)   queryString.append(", month(n1.createdOn)");
        if (projectDay)     queryString.append(", day(n1.createdOn)");
        queryString.append(" ");

        if (projectYear || projectMonth || projectDay) queryString.append("order by").append(" ");
        if (projectYear)    queryString.append("year(n1.createdOn) desc");
        if (projectMonth)   queryString.append(", month(n1.createdOn) desc");
        if (projectDay)     queryString.append(", day(n1.createdOn) desc");
        queryString.append(" ");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        /*
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        */
        nestedSetQuery.setParameter("clazz", "DOCUMENT"); // TODO: Hibernate can't bind the discriminator? Not even with Hibernate.CLASS type...
        if (ignoreNode.getId() != null)
            nestedSetQuery.setParameter("ignoreNode", ignoreNode);
        if (limitYear != null) nestedSetQuery.setParameter("limitYear", limitYear);
        if (limitMonth!= null) nestedSetQuery.setParameter("limitMonth", limitMonth);
        if (limitDay != null) nestedSetQuery.setParameter("limitDay", limitDay);
        if (tag != null && tag.length()>0) nestedSetQuery.setParameter("tag", "%" + tag + "%");

        nestedSetQuery.setResultTransformer(Transformers.aliasToBean(BlogEntryCount.class));

        return nestedSetQuery.list();
    }

    private Session getSession() {
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }

}
