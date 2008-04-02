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
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.action.Pager;
import org.jboss.seam.wiki.util.WikiUtil;

import java.io.Serializable;
import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("blogDirectory")
@Scope(ScopeType.CONVERSATION)
public class BlogDirectory implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In(create = true)
    BlogDAO blogDAO;

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    @In("#{preferences.get('Blog', currentMacro)}")
    BlogPreferences prefs;

    private Pager pager;

    private Integer year;
    private Integer month;
    private Integer day;
    private String tag;

    @RequestParameter
    public void setPage(Integer page) {
        if (pager == null) pager = new Pager(prefs.getPageSize());
        pager.setPage(page);
    }

    public Pager getPager() {
        return pager;
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

    private List<BlogEntry> blogEntries;
    private List<BlogEntryCount> blogEntryCountsByYearAndMonth;
    // Need to expose this as a datamodel so Seam can convert our map to a collection of Map.Entry objects
    @DataModel
    private Map<Date, List<BlogEntry>> recentBlogEntries;

    @Observer(value = {"PersistenceContext.filterReset"}, create = false)
    public void loadBlogEntries() {
        pager.setPageSize(prefs.getPageSize());
        pager.setNumOfRecords(blogDAO.countBlogEntries(currentDirectory, currentDocument, year, month, day, tag));
        if (pager.getNumOfRecords() == 0) {
            blogEntries = Collections.EMPTY_LIST;
            return;
        }
        blogEntries =
            blogDAO.findBlogEntriesInDirectory(
                    currentDirectory,
                    currentDocument,
                    pager,
                    year, month, day,
                    tag,
                    true
            );
    }


    @Observer(value = {"Macro.render.blogArchive", "PersistenceContext.filterReset"}, create = false)
    public void loadBlogEntryCountsByYearAndMonth() {
        blogEntryCountsByYearAndMonth = blogDAO.countAllBlogEntriesGroupByYearMonth(currentDirectory, currentDocument, tag);
    }

    @Factory(value = "recentBlogEntries")
    @Observer(value = {"Macro.render.blogRecentEntries", "PersistenceContext.filterReset"}, create = false)
    public void loadRecentBlogEntries() {
        List<BlogEntry> recentBlogEntriesNonAggregated =
            blogDAO.findBlogEntriesInDirectory(
                    currentDirectory,
                    currentDocument,
                    new Pager(prefs.getRecentEntriesItems()),
                    null, null, null,
                    null, false
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

    public List<BlogEntry> getBlogEntries() {
        if (blogEntries == null) loadBlogEntries();
        return blogEntries;
    }

    public List<BlogEntryCount> getBlogEntryCountsByYearAndMonth() {
        if (blogEntryCountsByYearAndMonth == null) loadBlogEntryCountsByYearAndMonth();
        return blogEntryCountsByYearAndMonth;
    }

    public String getDateUrl() {
        return WikiUtil.dateAsString(year, month, day);
    }

    public String getTagUrl() {
        return tag != null && tag.length()>0 ? "/Tag/" + WikiUtil.encodeURL(tag) : "";
    }

}
