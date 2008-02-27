/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blogdirectory;

import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.transform.ResultTransformer;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.ScopeType;

import javax.persistence.EntityManager;
import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("blogDAO")
@Scope(ScopeType.CONVERSATION)
public class BlogDAO implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    @In
    Integer currentAccessLevel;

    // Too bad, but we really need a SQL query here... better use SQL queries for ALL queries in this DAO and generalize things
    private String[] getWikiDocumentSQLColumnNames() {
        return new String[]{
            "doc2.NODE_ID",
            "doc2.OBJ_VERSION", "doc2.PARENT_NODE_ID", "doc2.RATING",
            "doc2.AREA_NR", "doc2.NAME", "doc2.WIKINAME", "doc2.CREATED_BY_USER_ID", "doc2.CREATED_ON", "doc2.WRITE_PROTECTED",
            "doc2.LAST_MODIFIED_BY_USER_ID", "doc2.LAST_MODIFIED_ON", "doc2.READ_ACCESS_LEVEL", "doc2.WRITE_ACCESS_LEVEL",
            "doc1.FILE_REVISION",
            "doc.NAME_AS_TITLE", "doc.ENABLE_COMMENTS", "doc.ENABLE_COMMENT_FORM", "doc.ENABLE_COMMENTS_ON_FEEDS",
            "doc.HEADER", "doc.HEADER_MACROS", "doc.CONTENT", "doc.CONTENT_MACROS", "doc.FOOTER", "doc.FOOTER_MACROS"
        };
    }

    private String getblogEntryFromClause(String tag) {
        StringBuilder clause = new StringBuilder();
        clause.append("from WIKI_DOCUMENT doc").append(" ");
        clause.append("inner join WIKI_FILE doc1 on doc.NODE_ID=doc1.NODE_ID").append(" ");
        clause.append("inner join WIKI_NODE doc2 on doc.NODE_ID=doc2.NODE_ID").append(" ");
        if (tag != null && tag.length() > 0) clause.append("inner join WIKI_TAG t on t.FILE_ID = doc1.NODE_ID").append(" ");
        return clause.toString();
    }

    private String getBlogEntryWhereClause(WikiDocument ignoreDoc, Integer year, Integer month, Integer day, String tag) {
        StringBuilder clause = new StringBuilder();
        clause.append("where doc2.PARENT_NODE_ID in").append(" (");
        clause.append("select distinct dir1.NODE_ID from WIKI_DIRECTORY dir1, WIKI_DIRECTORY dir2").append(" ");
        clause.append("where dir1.NS_THREAD = dir2.NS_THREAD").append(" ");
        clause.append("and dir1.NS_LEFT between dir2.NS_LEFT and dir2.NS_RIGHT").append(" ");
        clause.append("and dir2.NS_THREAD=:nsThread and dir2.NS_LEFT>=:nsLeft and dir2.NS_RIGHT<=:nsRight");
        clause.append(") ");
        clause.append("and doc.HEADER_MACROS like '%blogEntry%'").append(" ");
        clause.append("and doc2.READ_ACCESS_LEVEL <= :currentAccessLevel").append(" ");
        if (ignoreDoc != null && ignoreDoc.getId() != null) clause.append("and doc.NODE_ID<>:ignoreDoc").append(" ");
        if (tag != null && tag.length()>0)                  clause.append("and t.TAG = :tag").append(" ");
        if (year != null)   clause.append("and year(doc2.CREATED_ON) = :limitYear").append(" ");
        if (month != null)  clause.append("and month(doc2.CREATED_ON) = :limitMonth").append(" ");
        if (day != null)    clause.append("and day(doc2.CREATED_ON) = :limitDay").append(" ");
        return clause.toString();
    }

    private void bindBlogEntryWhereClause(Query query, WikiDirectory startDir, WikiDocument ignoreDoc,
                                          Integer year, Integer month, Integer day, String tag) {
        query.setParameter("nsThread", startDir.getNodeInfo().getNsThread());
        query.setParameter("nsLeft", startDir.getNodeInfo().getNsLeft());
        query.setParameter("nsRight", startDir.getNodeInfo().getNsRight());
        query.setParameter("currentAccessLevel", currentAccessLevel);

        if (ignoreDoc != null && ignoreDoc.getId() != null) query.setParameter("ignoreDoc", ignoreDoc);
        if (tag != null && tag.length()>0)                  query.setParameter("tag", tag);
        if (year != null)   query.setParameter("limitYear", year);
        if (month != null)  query.setParameter("limitMonth", month);
        if (day != null)    query.setParameter("limitDay", day);
    }

    public List<BlogEntry> findBlogEntriesWithCommentCount(WikiDirectory startDir,
                                                           WikiDocument ignoreDoc,
                                                           Pager pager,
                                                           Integer year,
                                                           Integer month,
                                                           Integer day,
                                                           String tag) {

        StringBuilder queryString = new StringBuilder();

        queryString.append("select").append(" ");
        for (int i = 0; i < getWikiDocumentSQLColumnNames().length; i++) {
            queryString.append(getWikiDocumentSQLColumnNames()[i]);
            if (i != getWikiDocumentSQLColumnNames().length-1) queryString.append(", ");
        }
        queryString.append(", count(c3.NODE_ID) as COMMENT_COUNT").append(" ");
        queryString.append(getblogEntryFromClause(tag));
        queryString.append("left outer join WIKI_NODE c1 on doc.NODE_ID = c1.PARENT_NODE_ID").append(" ");
        queryString.append("left outer join WIKI_COMMENT c2 on c1.NODE_ID = c2.NODE_ID").append(" ");
        queryString.append("left outer join WIKI_COMMENT c3 on c2.NS_THREAD = c3.NS_THREAD").append(" ");
        queryString.append(getBlogEntryWhereClause(ignoreDoc, year, month, day, tag));

        queryString.append("group by").append(" ");
        for (int i = 0; i < getWikiDocumentSQLColumnNames().length; i++) {
            queryString.append(getWikiDocumentSQLColumnNames()[i]);
            if (i != getWikiDocumentSQLColumnNames().length-1) queryString.append(", ");
        }
        queryString.append(" ");
        queryString.append("order by doc2.CREATED_ON desc");

        SQLQuery query = getSession().createSQLQuery(queryString.toString());

        bindBlogEntryWhereClause(query, startDir, ignoreDoc, year, month, day, tag);

        query.setComment("Finding all blogEntry documents recursively in dir: " + startDir.getName());
        query.addEntity(WikiDocument.class);
        query.addScalar("COMMENT_COUNT", Hibernate.LONG);
        query.setFirstResult( pager.getQueryFirstResult() );
        query.setMaxResults( pager.getQueryMaxResults() );

        query.setResultTransformer(
            new ResultTransformer() {
                public Object transformTuple(Object[] result, String[] aliases) {
                    BlogEntry be = new BlogEntry();
                    be.setEntryDocument( (WikiDocument)result[0]);
                    be.setCommentCount( (Long)result[1] );
                    return be;
                }
                public List transformList(List list) { return list; }
            }
        );

        return (List<BlogEntry>)query.list();

    }

    public Long countBlogEntries(WikiDirectory startDir, WikiDocument ignoreDoc, Integer year, Integer month, Integer day, String tag) {
        return countBlogEntries(startDir, ignoreDoc, false, false, false, year, month, day, tag).get(0).getNumOfEntries();
    }

    public List<BlogEntryCount> countAllBlogEntriesGroupByYearMonth(WikiDirectory startDir, WikiDocument ignoreDoc, String tag) {
        return countBlogEntries(startDir, ignoreDoc, true, true, false, null, null, null, tag);
    }

    private List<BlogEntryCount> countBlogEntries(WikiDirectory startDir, WikiDocument ignoreDoc,
                                                 final boolean projectYear, final boolean projectMonth, final boolean projectDay,
                                                 Integer limitYear, Integer limitMonth, Integer limitDay,
                                                 String tag) {

        // Sanity input check
        if (projectDay && (!projectMonth || !projectYear))
            throw new IllegalArgumentException("Can't project on day without months or year");
        if (projectMonth && !projectYear)
            throw new IllegalArgumentException("Can't project on month without year");

        StringBuilder queryString = new StringBuilder();

        queryString.append("select count(doc.NODE_ID) as NUM_OF_ENTRIES").append(" ");
        if (projectYear) queryString.append(", ").append("year(doc2.CREATED_ON) as YEAR");
        if (projectMonth) queryString.append(", ").append("month(doc2.CREATED_ON) as MONTH");
        if (projectDay) queryString.append(", ").append("day(doc2.CREATED_ON) as DAY");
        queryString.append(" ");

        queryString.append(getblogEntryFromClause(tag));
        queryString.append(getBlogEntryWhereClause(ignoreDoc, limitYear, limitMonth, limitDay, tag));

        if (projectYear || projectMonth || projectDay) queryString.append("group by").append(" ");
        if (projectYear)    queryString.append("year(doc2.CREATED_ON)");
        if (projectMonth)   queryString.append(", month(doc2.CREATED_ON)");
        if (projectDay)     queryString.append(", day(doc2.CREATED_ON)");

        if (projectYear || projectMonth || projectDay) queryString.append("order by").append(" ");
        if (projectYear)    queryString.append("YEAR desc");
        if (projectMonth)   queryString.append(", MONTH desc");
        if (projectDay)     queryString.append(", DAY desc");

        SQLQuery query = getSession().createSQLQuery(queryString.toString());

        bindBlogEntryWhereClause(query, startDir, ignoreDoc, limitYear, limitMonth, limitDay, tag);

        query.setComment("Finding blogEntry counts");
        query.addScalar("NUM_OF_ENTRIES", Hibernate.LONG);
        if (projectYear)    query.addScalar("YEAR", Hibernate.INTEGER);
        if (projectMonth)   query.addScalar("MONTH", Hibernate.INTEGER);
        if (projectDay)     query.addScalar("DAY", Hibernate.INTEGER);

        query.setResultTransformer(
            new ResultTransformer() {
                public Object transformTuple(Object[] result, String[] aliases) {
                    BlogEntryCount beCount = new BlogEntryCount();
                    beCount.setNumOfEntries( (Long)result[0] );
                    if (projectYear)    beCount.setYear( (Integer)result[1] );
                    if (projectMonth)   beCount.setMonth( (Integer)result[2] );
                    if (projectDay)     beCount.setDay( (Integer)result[3] );
                    return beCount;
                }
                public List transformList(List list) { return list; }
            }
        );

        return (List<BlogEntryCount>) query.list();
    }

    private Session getSession() {
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }

}
