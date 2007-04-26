package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

@Entity
@Table(name = "FEEDENTRY")
public class FeedEntry implements Serializable, Comparable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEEDENTRY_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION")
    protected Integer version;

    @Column(name = "LINK", nullable = false)
    @Length(min = 3, max = 1024)
    private String link;

    @Column(name = "TITLE", nullable = false)
    @Length(min = 3, max = 255)
    private String title;

    @Column(name = "AUTHOR", nullable = false)
    @Length(min = 3, max = 255)
    private String author;

    @Column(name = "PUBLISHED_ON", nullable = false)
    private Date publishedDate = new Date();

    @Column(name = "UPDATED_ON", nullable = false)
    private Date updatedDate;

    @Column(name = "DESCRIPTION_TYPE", nullable = false)
    @Length(min = 3, max = 255)
    private String descriptionType;

    @Column(name = "DESCRIPTION_VALUE", nullable = false)
    @Length(min = 1, max = 32768)
    private String descriptionValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID")
    @org.hibernate.annotations.ForeignKey(name = "FK_FEEDENTRY_DOCUMENT_ID")
    private Document document;

    public FeedEntry() {}

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

    // Mutable properties

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getDescriptionType() {
        return descriptionType;
    }

    public void setDescriptionType(String descriptionType) {
        this.descriptionType = descriptionType;
    }

    public String getDescriptionValue() {
        return descriptionValue;
    }

    public void setDescriptionValue(String descriptionValue) {
        this.descriptionValue = descriptionValue;
    }

    public String getDescriptionValueStripped() {
        return getDescriptionValue().replaceAll("\\<.*?\\>","");
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    // Sort by date
    public int compareTo(Object o) {
        return ((FeedEntry)o).getUpdatedDate().compareTo(getUpdatedDate());
    }

    public String toString() {
        return "FeedEntry: " + getId();
    }
}
