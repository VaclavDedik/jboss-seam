package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Document;

import java.util.*;
import java.io.Serializable;

@Name("blogDirectory")
@Scope(ScopeType.PAGE)
public class BlogDirectory implements Serializable {

    @In
    NodeDAO nodeDAO;

    @In
    Directory currentDirectory;

    @In
    Document currentDocument;

    @RequestParameter
    private void setBlogPage(Integer blogPage) {
        if (blogPage != null) this.page = blogPage;
    }

    @RequestParameter
    private Integer day;
    @RequestParameter
    private Integer month;
    @RequestParameter
    private Integer year;

    private List<BlogEntry> blogEntries;

    private String orderByProperty;
    private boolean orderDescending;
    private int totalRowCount;
    private int page;
    @In("#{blogDirectoryPreferences.properties['pageSize']}")
    private long pageSize;
    private Calendar startDate;
    private Calendar endDate;

    @Create
    public void initialize() {
        Calendar today = new GregorianCalendar();
        if (day == null) day = today.get(Calendar.DAY_OF_MONTH);
        if (month == null) month = today.get(Calendar.MONTH);
        if (year == null) year = today.get(Calendar.YEAR);

        orderByProperty = "createdOn";
        orderDescending = true;
        refreshBlogEntries();
    }

    private void queryRowCount() {
        totalRowCount = nodeDAO.getRowCountWithParent(Document.class, currentDirectory, currentDocument);
    }

    private void queryBlogEntries() {
        List<Document> documents =
                nodeDAO.findWithParent(Document.class, currentDirectory, currentDocument,
                                       orderByProperty, orderDescending, page * pageSize, pageSize);

        for (Document document : documents) {
            blogEntries.add(new BlogEntry(document));
        }
    }

    @Observer("Preferences.blogDirectoryPreferences")
    public void refreshBlogEntries() {
        blogEntries = new ArrayList<BlogEntry>();
        queryRowCount();
        if (totalRowCount != 0) queryBlogEntries();
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
