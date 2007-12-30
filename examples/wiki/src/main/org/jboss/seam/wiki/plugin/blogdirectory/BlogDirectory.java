/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.Preferences;

import java.io.Serializable;
import java.util.*;

@Name("blogDirectory")
@Scope(ScopeType.PAGE)
public class BlogDirectory implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    BlogDAO blogDAO;

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    private Integer page = 0;
    private Integer year;
    private Integer month;
    private Integer day;
    private String tag;

    @RequestParameter
    public void setPage(Integer page) {
        if (page != null) {
            this.page = page;
        }
    }
    @RequestParameter
    public void setYear(Integer year) {
        this.year = year;
    }
    @RequestParameter
    public void setMonth(Integer month) {
        this.month = month;
    }
    @RequestParameter
    public void setDay(Integer day) {
        this.day = day;
    }
    @RequestParameter
    public void setTag(String tag) {
        this.tag = tag;
    }

    private long numOfBlogEntries;
    private long totalNumOfBlogEntries;
    private List<BlogEntry> blogEntries;
    private List<BlogEntryCount> blogEntryCountsByYearAndMonth;
    // Need to expose this as a datamodel so Seam can convert our map to a collection of Map.Entry objects
    @DataModel
    private Map<Date, List<BlogEntry>> recentBlogEntries;

    private long pageSize;
    private long recentBlogEntriesCount;

    @Create
    public void initialize() {
        initializePreferences();
        refreshBlogEntries();
    }

    // Lazier than @In, would be too many injections because of c:forEach iteration on blog entry list
    private void initializePreferences() {
        // TODO: Uhm, we have several macros that use this backend bean... that doesn't work
        pageSize = ((BlogPreferences) Preferences.getInstance("Blog", "currentMacro")).getPageSize();
        recentBlogEntriesCount = ((BlogPreferences) Preferences.getInstance("Blog", "currentMacro")).getRecentEntriesItems();
    }

    private void queryNumOfBlogEntries() {
        numOfBlogEntries = blogDAO.countBlogEntries(currentDirectory, currentDocument, year, month, day, tag);
    }

    private void queryBlogEntries() {
        blogEntries =
            blogDAO.findBlogEntriesWithCommentCount(
                    currentDirectory,
                    currentDocument,
                    "createdOn",
                    true,
                    page * pageSize,
                    pageSize,
                    year, month, day,
                    tag
            );
    }

    private void queryBlogEntryCountsByYearAndMonth() {
        blogEntryCountsByYearAndMonth = blogDAO.countAllBlogEntriesGroupByYearMonth(currentDirectory, currentDocument, tag);
        for (BlogEntryCount blogEntryCount : blogEntryCountsByYearAndMonth) {
            totalNumOfBlogEntries = totalNumOfBlogEntries + blogEntryCount.getNumOfEntries();
        }
    }

    @Factory(value = "recentBlogEntries")
    @Observer("PreferenceComponent.refresh.blogRecentEntriesPreferences")
    public void queryRecentBlogEntries() {
        initializePreferences();
        List<BlogEntry> recentBlogEntriesNonAggregated =
            blogDAO.findBlogEntriesWithCommentCount(
                    currentDirectory,
                    currentDocument,
                    "createdOn",
                    true,
                    0,
                    recentBlogEntriesCount,
                    null, null, null,
                    null
            );

        // Now aggregate by day
        recentBlogEntries = new LinkedHashMap<Date, List<BlogEntry>>();
        for (BlogEntry blogEntry : recentBlogEntriesNonAggregated) {

            // Find the day (ignore the hours, minutes, etc.)
            Calendar createdOn = new GregorianCalendar();
            createdOn.setTime(blogEntry.getEntryDocument().getCreatedOn());
            GregorianCalendar createdOnDay = new GregorianCalendar(
                createdOn.get(Calendar.YEAR), createdOn.get(Calendar.MONTH), createdOn.get(Calendar.DAY_OF_MONTH)
            );
            Date createdOnDate = createdOnDay.getTime(); // Jesus, this API is just bad...

            // Aggregate by day
            List<BlogEntry> entriesForDay =
                recentBlogEntries.containsKey(createdOnDate)
                ? recentBlogEntries.get(createdOnDate)
                : new ArrayList<BlogEntry>();

            entriesForDay.add(blogEntry);
            recentBlogEntries.put(createdOnDate, entriesForDay);
        }
    }

    @Observer("PreferenceComponent.refresh.blogDirectoryPreferences")
    public void refreshBlogEntries() {
        initializePreferences();
        blogEntries = new ArrayList<BlogEntry>();
        queryNumOfBlogEntries();
        if (numOfBlogEntries != 0){
            queryBlogEntries();
        }
    }

    public long getTotalNumOfBlogEntries() {
        if (blogEntryCountsByYearAndMonth == null) {
            queryBlogEntryCountsByYearAndMonth();
        }
        return totalNumOfBlogEntries;
    }

    public long getNumOfBlogEntries() {
        return numOfBlogEntries;
    }

    public List<BlogEntry> getBlogEntries() {
        return blogEntries;
    }

    public List<BlogEntryCount> getBlogEntryCountsByYearAndMonth() {
        if (blogEntryCountsByYearAndMonth == null) {
            queryBlogEntryCountsByYearAndMonth();
        }
        return blogEntryCountsByYearAndMonth;
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
        return (page * pageSize + pageSize) > numOfBlogEntries
                ? numOfBlogEntries
                : page * pageSize + pageSize;
    }

    public long getLastPage() {
        long lastPage = (numOfBlogEntries / pageSize);
        if (numOfBlogEntries % pageSize == 0) lastPage--;
        return lastPage;
    }

    public boolean isNextPageAvailable() {
        return blogEntries != null && numOfBlogEntries > ((page * pageSize) + pageSize);
    }

    public boolean isPreviousPageAvailable() {
        return blogEntries != null && page > 0;
    }

    public String getDateUrl() {
        return dateAsString(year, month, day);
    }

    public String getTagUrl() {
        return tag != null && tag.length()>0 ? "/Tag/" + WikiUtil.encodeURL(tag) : "";
    }

    // Utilities

    public static String dateAsString(Integer year, Integer month, Integer day) {
        StringBuilder dateUrl = new StringBuilder();
        if (year != null) dateUrl.append("/").append(year);
        if (month != null) dateUrl.append("/").append(padInteger(month, 2));
        if (day != null) dateUrl.append("/").append(padInteger(day, 2));
        return dateUrl.toString();
    }

    private static String padInteger(Integer raw, int padding) {
        String rawInteger = raw.toString();
        StringBuilder paddedInteger = new StringBuilder( );
        for ( int padIndex = rawInteger.length() ; padIndex < padding; padIndex++ ) {
            paddedInteger.append('0');
        }
        return paddedInteger.append( rawInteger ).toString();
    }

}
