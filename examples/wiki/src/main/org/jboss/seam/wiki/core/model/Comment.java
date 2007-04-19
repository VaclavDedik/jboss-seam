package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.hibernate.validator.Email;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.User;

import javax.persistence.*;
import java.util.Date;
import java.io.Serializable;

@Entity
@Table(name = "COMMENTS")
public class Comment implements Serializable {

    @Id
    @GeneratedValue(generator = "wikiSequenceGenerator")
    @Column(name = "COMMENT_ID")
    private Long id = null;

    @Version
    @Column(name = "OBJ_VERSION")
    private int version = 0;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "DOCUMENT_ID", nullable = false)
    private Document document;

    @Column(name = "SUBJECT", nullable = false)
    @Length(min = 3, max = 255)
    private String subject;

    @Column(name = "FROM_USER_NAME", nullable = false)
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
    //@Length(min = 1, max = 32768)
    private String text;

    @Column(name = "CREATED_ON", nullable = false, updatable = false)
    private Date createdOn = new Date();
    
    public Comment () {}

    // Immutable properties

    public Long getId() { return id; }
    public Integer getVersion() { return version; }
    public Date getCreatedOn() { return createdOn; }

    // Mutable properties

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

    // Misc methods

    public String toString() {
        return  "Comment ('" + getId() + "'), " +
                "Subject: '" + getSubject() + "'";
    }
}
