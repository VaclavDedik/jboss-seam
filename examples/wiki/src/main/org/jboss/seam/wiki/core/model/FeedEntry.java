/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(
    name = "FEEDENTRY",
    uniqueConstraints = {
        // An entry can either be for a document (DOCUMENT_ID 123, COMMENT_IDENTIFIER null)
        // or a comment (DOCUMENT_ID 123, COMMENT_IDENTIFIER 456). Duplicate comment identifiers
        // for the same document are not allowed.
        @UniqueConstraint(columnNames = {"DOCUMENT_ID", "COMMENT_IDENTIFIER"})
    }
)
public class FeedEntry implements Serializable, Comparable {

    public static final String END_TEASER_MACRO = "endTeaser";
    public static final String END_TEASER_MARKER = "[<=endTeaser]";

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEEDENTRY_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected Integer version;

    @Column(name = "LINK", nullable = false)
    @Length(min = 3, max = 1024)
    private String link;

    @Column(name = "TITLE", nullable = false)
    @Length(min = 3, max = 1024)
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
    @JoinColumn(name = "DOCUMENT_ID", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_FEEDENTRY_DOCUMENT_ID")
    private Document document;

    @Column(name = "COMMENT_IDENTIFIER", nullable = true)
    private Long commentIdentifier;

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
        return stripHTMLTags(getDescriptionValue());
    }

    public boolean isTeaserMarkerPresent() {
        return getDescriptionValueStripped().contains(END_TEASER_MARKER);
    }

    public String getTeaserStripped() {
        String stripped = getDescriptionValueStripped();
        return isTeaserMarkerPresent() ? stripped.substring(0, stripped.indexOf(END_TEASER_MARKER)) : stripped;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public Long getCommentIdentifier() {
        return commentIdentifier;
    }

    public void setCommentIdentifier(Long commentIdentifier) {
        this.commentIdentifier = commentIdentifier;
    }

    // Sort by date
    public int compareTo(Object o) {
        FeedEntry other = (FeedEntry)o;
        if (getPublishedDate().getTime() > other.getPublishedDate().getTime()) return -1;
        return (getPublishedDate().getTime() == other.getPublishedDate().getTime() ? 0 : 1);
    }

    public String toString() {
        return "FeedEntry: " + getId();
    }

    private String stripHTMLTags(String original) {
        return original.replaceAll("\\<([a-zA-Z]|/){1}?.*?\\>","");
    }
}
