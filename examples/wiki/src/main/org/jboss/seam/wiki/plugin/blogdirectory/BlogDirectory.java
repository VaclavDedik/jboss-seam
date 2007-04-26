package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Document;

import java.io.Serializable;
import java.util.*;

@Name("blogDirectory")
@Scope(ScopeType.PAGE)
public class BlogDirectory implements Serializable {

    @In
    NodeDAO nodeDAO;

    @In
    FacesMessages facesMessages;

    @In
    Directory currentDirectory;

    @In
    Document currentDocument;

    @RequestParameter
    Boolean blogIndex;

    @RequestParameter
    private void setBlogPage(Integer blogPage) {
        if (blogPage != null) this.page = blogPage;
    }

    private List<BlogEntry> blogEntries;

    // Need to expose this as a datamodel so Seam can convert our map to a bunch of Map.Entry objects
    @DataModel
    private Map<Date, List<BlogEntry>> recentBlogEntries;
    @DataModel
    private Map<Date, List<BlogEntry>> allBlogEntries;
   
    private String orderByProperty;
    private boolean orderDescending;
    private int totalRowCount;
    private int page;
    @In("#{blogDirectoryPreferences.properties['pageSize']}")
    private long pageSize;
    @In("#{blogDirectoryPreferences.properties['recentHeadlines']}")
    private long recentBlogEntriesCount;

    @Create
    public void initialize() {
        orderByProperty = "createdOn";
        orderDescending = true;
        refreshBlogEntries();
    }

    private void queryRowCount() {
        totalRowCount = nodeDAO.getRowCountWithParent(Document.class, currentDirectory, currentDocument);
    }

    private void queryBlogEntries() {
        // TODO: This could be done in one query but I'm too lazy to write the GROUP BY clause because Hibernate doesn't do it for me
        List<Document> documents =
                nodeDAO.findWithParent(Document.class, currentDirectory, currentDocument,
                                       orderByProperty, orderDescending, page * pageSize, pageSize);
        Map<Long,Long> commentCounts = nodeDAO.findCommentCount(currentDirectory);

        for (Document document : documents) {
            blogEntries.add(
                new BlogEntry(document, commentCounts.get(document.getId()) )
            );
        }
    }

    private void queryRecentBlogEntries() {
        List<Document> documents =
                nodeDAO.findWithParent(Document.class, currentDirectory, currentDocument, "createdOn", true, 0, recentBlogEntriesCount);

        recentBlogEntries = new LinkedHashMap<Date, List<BlogEntry>>();
        for (Document document : documents) {

            // Find the day (ignore the hours, minutes, etc.)
            Calendar createdOn = new GregorianCalendar();
            createdOn.setTime(document.getCreatedOn());
            GregorianCalendar createdOnDay = new GregorianCalendar(
                createdOn.get(Calendar.YEAR), createdOn.get(Calendar.MONTH), createdOn.get(Calendar.DAY_OF_MONTH)
            );
            Date createdOnDate = createdOnDay.getTime(); // Jesus, this API is just bad...

            // Aggregate by day
            List<BlogEntry> entriesForDay =
                recentBlogEntries.containsKey(createdOnDate)
                ? recentBlogEntries.get(createdOnDate)
                : new ArrayList<BlogEntry>();

            entriesForDay.add(new BlogEntry(document));
            recentBlogEntries.put(createdOnDate, entriesForDay);
        }
    }

    private void queryAllBlogEntries() {
        if (blogIndex == null || !blogIndex) return;
        List<Document> documents =
                nodeDAO.findWithParent(Document.class, currentDirectory, currentDocument, "createdOn", true, 0, 0);

        allBlogEntries = new LinkedHashMap<Date, List<BlogEntry>>();
        for (Document document : documents) {

            // Find the month (ignore the days, hours, minutes, etc.)
            Calendar createdOn = new GregorianCalendar();
            createdOn.setTime(document.getCreatedOn());
            GregorianCalendar createdOnMonth = new GregorianCalendar(
                createdOn.get(Calendar.YEAR), createdOn.get(Calendar.MONTH), 1
            );
            Date createdOnDate = createdOnMonth.getTime(); // Jesus, this API is just bad...

            // Aggregate by month
            List<BlogEntry> entriesForMonth =
                allBlogEntries.containsKey(createdOnDate)
                ? allBlogEntries.get(createdOnDate)
                : new ArrayList<BlogEntry>();

            entriesForMonth.add(new BlogEntry(document));
            allBlogEntries.put(createdOnDate, entriesForMonth);
        }
    }

    @Observer("Preferences.blogDirectoryPreferences")
    public void refreshBlogEntries() {
        blogEntries = new ArrayList<BlogEntry>();
        queryRowCount();
        if (totalRowCount != 0) {
            queryBlogEntries();
            queryRecentBlogEntries();
            queryAllBlogEntries();
        }
    }

    public List<BlogEntry> getBlogEntries() {
        return blogEntries;
    }

    public int getTotalRowCount() {
        return totalRowCount;
    }

    public int getNextPage() {
        return page + 1;
    }

    public int getPreviousPage() {
        return page - 1;
    }

    public int getFirstPage() {
        return 0;
    }

    public long getFirstRow() {
        return page * pageSize + 1;
    }

    public long getLastRow() {
        return (page * pageSize + pageSize) > totalRowCount
                ? totalRowCount
                : page * pageSize + pageSize;
    }

    public long getLastPage() {
        long lastPage = (totalRowCount / pageSize);
        if (totalRowCount % pageSize == 0) lastPage--;
        return lastPage;
    }

    public boolean isNextPageAvailable() {
        return blogEntries != null && totalRowCount > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return blogEntries != null && page > 0;
    }
}
