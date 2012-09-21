package domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;


/**
 * Represents a blog, containing zero or more blog entries.
 *
 * @author    Simon Brown
 */
@Entity
@Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
public class Blog {

  /** the name of the blog */
  @Id @Size(max=70)
  private String name;

  /** the description of the blog */
  @Size(max=150) @NotNull
  private String description;

  /** the locale of the blog */
  @NotNull
  private Locale locale;

  /** the timezone of the blog */
  @NotNull
  private TimeZone timeZone;
  
  @Size(min=5, max=10) @NotNull
  private String password;
  
  @OneToOne(optional=false, mappedBy="blog")
  private HitCount hitCount;

  /** the list of blog entries, in reverse chronological order */
  @OneToMany(mappedBy="blog") 
  @OrderBy("date desc")
  @Cache(usage=CacheConcurrencyStrategy.TRANSACTIONAL)
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

   public String getPassword()
   {
      return password;
   }

   public HitCount getHitCount()
   {
      return hitCount;
   }

}
