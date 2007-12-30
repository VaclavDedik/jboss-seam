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
@Table(name = "FEEDENTRY")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "FEEDENTRY_TYPE", length = 255)
@DiscriminatorValue("EXTERNAL")
public class FeedEntry implements Serializable, Comparable {

    public static final String END_TEASER_MACRO = "endTeaser";
    public static final String END_TEASER_MARKER = "[<=endTeaser]";

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "FEEDENTRY_ID")
    private Long id;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    protected int version = 0;

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

    public int getReadAccessLevel() {
        return 0; // No restrictions
    }

    public boolean isTagged(String tag) {
        return false; // Can't be tagged
    }

    // Sort by date
    public int compareTo(Object o) {
        FeedEntry other = (FeedEntry)o;
        if (getPublishedDate().getTime() > other.getPublishedDate().getTime()) return -1;
        return (getPublishedDate().getTime() == other.getPublishedDate().getTime() ? 0 : 1);
    }

    public String toString() {
        return "FeedEntry (" + getId() + ")";
    }

    private String stripHTMLTags(String original) {
        return original.replaceAll("\\<([a-zA-Z]|/){1}?.*?\\>","");
    }

}
