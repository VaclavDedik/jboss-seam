/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.Email;
import org.jboss.seam.wiki.core.search.annotations.*;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "COMMENTS")

@org.hibernate.annotations.BatchSize(size = 10)

@org.hibernate.search.annotations.Indexed
@Searchable(description = "Comments")
@CompositeSearchables(
    @CompositeSearchable(
        description = "Content", type = SearchableType.PHRASE,
        properties = {"subject", "text"}
    )
)
// TODO: We should have a CHECK constraint here that fails if (FROM_USER_ID is null and FROM_USER_NAME is null)
// However, MySQL doesn't support constraints properly...
public class Comment implements Serializable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "COMMENT_ID")
    @org.hibernate.search.annotations.DocumentId(name = "commentId")
    private Long id = null;

    @Version
    @Column(name = "OBJ_VERSION", nullable = false)
    private int version = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DOCUMENT_ID", nullable = false)
    @org.hibernate.annotations.ForeignKey(name = "FK_COMMENT_DOCUMENT_ID")
    private Document document;

    @Column(name = "SUBJECT", nullable = false)
    @Length(min = 3, max = 255)
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FROM_USER_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_COMMENT_FROM_USER_ID")
    private User fromUser;

    @Column(name = "FROM_USER_NAME", nullable = true)
    @Length(min = 3, max = 100)
    private String fromUserName;

    @Column(name = "FROM_USER_EMAIL", nullable = true)
    @Length(min = 0, max = 255)
    @Email
    private String fromUserEmail;

    @Column(name = "FROM_USER_HOMEPAGE", nullable = true)
    @Length(min = 0, max = 1000)
    private String fromUserHomepage;

    @Column(name = "COMMENT_TEXT", nullable = false)
    @Length(min = 1, max = 32768)
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    private String text;

    @Column(name = "USE_WIKI_TEXT", nullable = false)
    private boolean useWikiText = true;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    @org.hibernate.search.annotations.Field(
        index = org.hibernate.search.annotations.Index.UN_TOKENIZED,
        store = org.hibernate.search.annotations.Store.YES
    )
    @org.hibernate.search.annotations.DateBridge(resolution = org.hibernate.search.annotations.Resolution.DAY)
    @Searchable(description = "Created", type = SearchableType.PASTDATE)
    private Date createdOn = new Date();

    public Comment () {}

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }

    // Mutable properties

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getFromUserEmail() {
        return fromUserEmail;
    }

    public void setFromUserEmail(String fromUserEmail) {
        this.fromUserEmail = fromUserEmail;
    }

    public String getFromUserHomepage() {
        return fromUserHomepage;
    }

    public void setFromUserHomepage(String fromUserHomepage) {
        this.fromUserHomepage = fromUserHomepage;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isUseWikiText() {
        return useWikiText;
    }

    public void setUseWikiText(boolean useWikiText) {
        this.useWikiText = useWikiText;
    }

    // Misc methods

    public String toString() {
        return  "Comment ('" + getId() + "'), " +
                "Subject: '" + getSubject() + "'";
    }
}
