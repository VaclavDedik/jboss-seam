package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Represents a blog, containing zero or more blog entries.
 *
 * @author    Simon Brown
 */
public class Blog {

  /** the name of the blog */
  private String name;

  /** the description of the blog */
  private String description;

  /** the locale of the blog */
  private Locale locale;

  /** the timezone of the blog */
  private TimeZone timeZone;

  /** the list of blog entries, in reverse chronological order */
  private List<BlogEntry> blogEntries;

  /**
   * Default, no args constructor.
   */
  public Blog() {
    this.blogEntries = new ArrayList<BlogEntry>();
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Locale getLocale() {
    return locale;
  }

  public void setLocale(Locale locale) {
    this.locale = locale;
  }

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone(TimeZone timeZone) {
    this.timeZone = timeZone;
  }

  public List<BlogEntry> getRecentBlogEntries() {
    if (blogEntries == null) {
      return new ArrayList<BlogEntry>();
    }

    int length = 3;
    if (blogEntries.size() < 3) {
      length = blogEntries.size();
    }
    return blogEntries.subList(0, length);
  }

  public List<BlogEntry> getBlogEntries() {
    return blogEntries;
  }

  public void setBlogEntries(List<BlogEntry> blogEntries) {
    this.blogEntries = blogEntries;
  }

  public void addBlogEntry(BlogEntry blogEntry) {
    blogEntries.add(blogEntry);
  }

  public BlogEntry getBlogEntry(String id) {
    for (BlogEntry blogEntry : blogEntries) {
      if (blogEntry.getId().equals(id)) {
        return blogEntry;
      }
    }

    return null;
  }

}
