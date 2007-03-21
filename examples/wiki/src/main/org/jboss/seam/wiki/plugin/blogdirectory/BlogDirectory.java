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
    private Integer page;
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
    private int rowCount;
    private int pageSize;
    private Calendar startDate;
    private Calendar endDate;

    @Factory("blogEntries")
    @Create
    public void initialize() {
        if (page == null) page = 0;
        Calendar today = new GregorianCalendar();
        if (day == null) day = today.get(Calendar.DAY_OF_MONTH);
        if (month == null) month = today.get(Calendar.MONTH);
        if (year == null) year = today.get(Calendar.YEAR);

        pageSize = 10;
        orderByProperty = "createdOn";
        orderDescending = true;

        blogEntries = new ArrayList<BlogEntry>();
        queryRowCount();
        if (rowCount != 0) queryBlogEntries();
    }

    private void queryRowCount() {
        rowCount = nodeDAO.getRowCountWithParent(Document.class, currentDirectory);
    }

    private void queryBlogEntries() {
        List<Document> documents =
                nodeDAO.findWithParent(Document.class, currentDirectory, orderByProperty, orderDescending, page * pageSize, pageSize);

        for (Document document : documents) {
            if (document.getId().equals(currentDocument.getId()))
                rowCount--;
            else 
                blogEntries.add(new BlogEntry(document));
        }
    }

    public int getRowCount() {
        return rowCount;
    }

    public List<BlogEntry> getBlogEntries() {
        return blogEntries;
    }

    public int getNextPage() {
        return page++;
    }

    public int getPreviousPage() {
        return page--;
    }

    public int firstPage() {
        return 0;
    }

    public int lastPage() {
        page = (rowCount / pageSize);
        if (rowCount % pageSize == 0) page--;
        return page;
    }

    public boolean isNextPageAvailable() {
        return blogEntries != null && rowCount > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return blogEntries != null && page > 0;
    }

}
