package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Document;

import java.util.*;

@Name("blogDirectory")
@Scope(ScopeType.CONVERSATION)
public class BlogDirectory {

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

    @DataModel(scope = ScopeType.PAGE)
    private List<BlogEntry> blogEntries;

    private String orderByProperty;
    private boolean orderDescending;
    private int totalRowCount;
    private int page;
    private int pageSize;
    private Calendar startDate;
    private Calendar endDate;

    @Factory("blogEntries")
    @Create
    public void initialize() {
        Calendar today = new GregorianCalendar();
        if (day == null) day = today.get(Calendar.DAY_OF_MONTH);
        if (month == null) month = today.get(Calendar.MONTH);
        if (year == null) year = today.get(Calendar.YEAR);

        pageSize = 3;
        orderByProperty = "createdOn";
        orderDescending = true;

        blogEntries = new ArrayList<BlogEntry>();
        queryRowCount();
        if (totalRowCount != 0) queryBlogEntries();
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

    public int getTotalRowCount() {
        return totalRowCount;
    }

    public List<BlogEntry> getBlogEntries() {
        return blogEntries;
    }

    public int getNextPage() {
        return ++page;
    }

    public int getPreviousPage() {
        return --page;
    }

    public int getFirstPage() {
        return 0;
    }

    public int getFirstRow() {
        return page * pageSize + 1;
    }

    public int getLastRow() {
        return (page * pageSize + pageSize) > totalRowCount
                ? totalRowCount
                : page * pageSize + pageSize;
    }

    public int getLastPage() {
        page = (totalRowCount / pageSize);
        if (totalRowCount % pageSize == 0) page--;
        return page;
    }

    public boolean isNextPageAvailable() {
        return blogEntries != null && totalRowCount > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return blogEntries != null && page > 0;
    }


}
