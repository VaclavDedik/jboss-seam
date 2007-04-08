package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "FEED")
public class Feed implements Serializable {

    @Id
    @GeneratedValue
    @Column(name = "FEED_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION")
    protected Integer version;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Column(name = "DESCRIPTION", nullable = true)
    private String description;

    @Column(name = "AUTHOR", nullable = true)
    private String author;

    @Column(name = "COPYRIGHT", nullable = true)
    private String copyright;

    @Column(name = "PUBLISHED_ON", nullable = false, updatable = false)
    private Date publishedDate = new Date();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DIRECTORY_ID", nullable = false, updatable = false)
    private Directory directory;

    public Feed() { }

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

    // Mutable properties

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Directory getDirectory() {
        return directory;
    }

    public void setDirectory(Directory directory) {
        this.directory = directory;
    }

    public String toString() {
        return "Feed: " + getId();
    }
}
