package domain;

import java.util.Date;

/**
 * Represents a blog entry.
 *
 * @author    Simon Brown
 */
public class BlogEntry {

  private String id;
  private String title;
  private String excerpt;
  private String body;
  private Date date;

  public BlogEntry(String id, String title, String excerpt, String body, Date date) {
    this.id = id;
    this.title = title;
    this.excerpt = excerpt;
    this.body = body;
    this.date = date;
  }

  public String getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getExcerpt() {
    return excerpt;
  }

  public String getBody() {
    return body;
  }

  public Date getDate() {
    return date;
  }

}
