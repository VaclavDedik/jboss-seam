/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.jboss.seam.wiki.core.search.annotations.Searchable;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;

@Entity
@DiscriminatorValue("DOCUMENT")
@org.hibernate.search.annotations.Indexed
@Searchable(description = "Documents")
@org.hibernate.annotations.BatchSize(size = 10)
@SecondaryTable(
    name = "NODE_DOCUMENT",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "DOCUMENT_ID")
)
@org.hibernate.annotations.Table(
    appliesTo = "NODE_DOCUMENT",
    foreignKey = @org.hibernate.annotations.ForeignKey(name = "FK_NODE_DOCUMENT_DOCUMENT_ID")
)
public class Document extends Node {

    @Column(table = "NODE_DOCUMENT", name = "CONTENT", nullable = false)
    @Length(min = 0, max = 32768)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    @org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.TOKENIZED)
    @Searchable(description = "Content")
    private String content;

    @Column(table = "NODE_DOCUMENT", name = "NAME_AS_TITLE")
    private Boolean nameAsTitle = true;

    @Column(table = "NODE_DOCUMENT", name = "ENABLE_COMMENTS")
    private Boolean enableComments = false;

    @Column(table = "NODE_DOCUMENT", name = "ENABLE_COMMENT_FORM")
    private Boolean enableCommentForm = true;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @org.hibernate.annotations.OrderBy(clause = "CREATED_ON desc")
    @org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
    private List<Comment> comments;

    @Column(table = "NODE_DOCUMENT", name = "PLUGINS_USED", nullable = false)
    private String pluginsUsed;

    public Document() {
        super("New Document");
        content = "Edit this text..."; // Don't know why the interactive preview doesn't work without content
    }

    public Document(String name) {
        super(name);
    }

    public Document(Document original) {
        super(original);
        this.content = original.content;
    }

    // Mutable properties

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    public boolean isNameAsTitle() {
        return nameAsTitle != null ? nameAsTitle.booleanValue() : false;
    }

    public void setNameAsTitle(boolean nameAsTitle) {
        this.nameAsTitle = nameAsTitle;
    }

    public Boolean getEnableComments() {
        return enableComments;
    }

    public void setEnableComments(Boolean enableComments) {
        this.enableComments = enableComments;
    }

    public Boolean getEnableCommentForm() {
        return enableCommentForm;
    }

    public void setEnableCommentForm(Boolean enableCommentForm) {
        this.enableCommentForm = enableCommentForm;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getPluginsUsed() {
        return pluginsUsed;
    }

    public void setPluginsUsed(String pluginsUsed) {
        this.pluginsUsed = pluginsUsed;
    }

    public void addChild(Node child) {
        throw new UnsupportedOperationException("Documents can't have children");
    }

    public Node removeChild(Node child) {
        throw new UnsupportedOperationException("Documents can't have children");
    }

    public void rollback(Node revision) {
        super.rollback(revision);
        this.content = ((Document)revision).content;
    }
}
