package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.AutoCreate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.transform.Transformers;

import javax.persistence.EntityManager;
import java.util.List;

@Name("blogDAO")
@AutoCreate
@Transactional
public class BlogDAO {

    @In
    protected EntityManager restrictedEntityManager;

    public List<BlogEntry> findBlogEntriesWithCommentCount(Node startNode,
                                                           Node ignoreNode,
                                                           String orderByProperty,
                                                           boolean orderDescending,
                                                           long firstResult,
                                                           long maxResults,
                                                           Integer year,
                                                           Integer month,
                                                           Integer day) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select n1 as entryDocument").append(", ");
        queryString.append("(select count(c) from Comment c where c.document.id = n1.id) as commentCount").append(" ");

        queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");
        queryString.append("fetch all properties").append(" ");
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        queryString.append("and n2.class = :clazz").append(" ");

        if (ignoreNode.getId() != null)
            queryString.append("and not n1 = :ignoreNode").append(" ");

        queryString.append("and not n1.pluginsUsed like '%blogDirectory%'").append(" ");

        if (year != null) queryString.append("and year(n1.createdOn) = :limitYear").append(" ");
        if (month != null) queryString.append("and month(n1.createdOn) = :limitMonth").append(" ");
        if (day != null) queryString.append("and day(n1.createdOn) = :limitDay").append(" ");

        queryString.append("group by").append(" ");
        for (int i = 0; i < startNode.getTreeSuperclassPropertiesForGrouping().length; i++) {
            queryString.append("n1.").append(startNode.getTreeSuperclassPropertiesForGrouping()[i]);
            if (i != startNode.getTreeSuperclassPropertiesForGrouping().length-1) queryString.append(", ");
        }
        queryString.append(" ");

        queryString.append("order by n1.").append(orderByProperty).append(" ");
        queryString.append( orderDescending ? "desc" : "asc").append("");

        Query nestedSetQuery = getSession().createQuery(queryString.toString());
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        nestedSetQuery.setParameter("clazz", "DOCUMENT"); // TODO: Hibernate can't bind the discriminator? Not even with Hibernate.CLASS type...
        if (ignoreNode.getId() != null)
            nestedSetQuery.setParameter("ignoreNode", ignoreNode);

        if (year != null) nestedSetQuery.setParameter("limitYear", year);
        if (month != null) nestedSetQuery.setParameter("limitMonth", month);
        if (day != null) nestedSetQuery.setParameter("limitDay", day);

        nestedSetQuery.setFirstResult( new Long(firstResult).intValue() );
        nestedSetQuery.setMaxResults( new Long(maxResults).intValue() );
        nestedSetQuery.setResultTransformer(Transformers.aliasToBean(BlogEntry.class));

        return (List<BlogEntry>)nestedSetQuery.list();
    }

    public Long countBlogEntries(Node startNode, Node ignoreNode, Integer year, Integer month, Integer day ) {
        return countBlogEntries(startNode, ignoreNode, false, false, false, year, month, day).get(0).getNumOfEntries();
    }

    public List<BlogEntryCount> countAllBlogEntriesGroupByYearMonth(Node startNode, Node ignoreNode) {
        return countBlogEntries(startNode, ignoreNode, true, true, false, null, null, null);
    }

    private List<BlogEntryCount> countBlogEntries(Node startNode, Node ignoreNode,
                                                 boolean projectYear, boolean projectMonth, boolean projectDay,
                                                 Integer limitYear, Integer limitMonth, Integer limitDay) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select count(n1.id) as numOfEntries");

        if (projectYear) queryString.append(", ").append("year(n1.createdOn) as year");
        if (projectMonth) queryString.append(", ").append("month(n1.createdOn) as month");
        if (projectDay) queryString.append(", ").append("day(n1.createdOn) as day");

        queryString.append(" ");
        queryString.append("from ").append(startNode.getTreeSuperclassEntityName()).append(" n1, ");
        queryString.append(startNode.getTreeSuperclassEntityName()).append(" n2 ");
        queryString.append("where n1.nsThread = :thread and n2.nsThread = :thread").append(" ");
        queryString.append("and n1.nsLeft between n2.nsLeft and n2.nsRight").append(" ");
        queryString.append("and n2.nsLeft > :startLeft and n2.nsRight < :startRight").append(" ");
        queryString.append("and n2.class = :clazz").append(" ");
        queryString.append("and not n1.pluginsUsed like '%blogDirectory%'").append(" ");

        if (ignoreNode.getId() != null)
            queryString.append("and not n1 = :ignoreNode").append(" ");

        if (limitYear != null) queryString.append("and year(n1.createdOn) = :limitYear").append(" ");
        if (limitMonth!= null) queryString.append("and month(n1.createdOn) = :limitMonth").append(" ");
        if (limitDay != null) queryString.append("and day(n1.createdOn) = :limitDay").append(" ");

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
        nestedSetQuery.setParameter("thread", startNode.getNsThread());
        nestedSetQuery.setParameter("startLeft", startNode.getNsLeft());
        nestedSetQuery.setParameter("startRight", startNode.getNsRight());
        nestedSetQuery.setParameter("clazz", "DOCUMENT"); // TODO: Hibernate can't bind the discriminator? Not even with Hibernate.CLASS type...
        if (ignoreNode.getId() != null)
            nestedSetQuery.setParameter("ignoreNode", ignoreNode);
        if (limitYear != null) nestedSetQuery.setParameter("limitYear", limitYear);
        if (limitMonth!= null) nestedSetQuery.setParameter("limitMonth", limitMonth);
        if (limitDay != null) nestedSetQuery.setParameter("limitDay", limitDay);

        nestedSetQuery.setResultTransformer(Transformers.aliasToBean(BlogEntryCount.class));

        return nestedSetQuery.list();
    }

    private Session getSession() {
        restrictedEntityManager.joinTransaction();
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }

}
