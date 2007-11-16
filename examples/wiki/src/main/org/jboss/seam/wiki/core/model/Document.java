/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.core.search.annotations.SearchableType;

import javax.persistence.*;
import javax.persistence.Entity;
import java.util.List;
import java.util.ArrayList;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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

    @Column(table = "NODE_DOCUMENT", name = "NAME_AS_TITLE", nullable = false)
    private Boolean nameAsTitle = true;

    @Column(table = "NODE_DOCUMENT", name = "ENABLE_COMMENTS", nullable = false)
    private Boolean enableComments = false;

    @Column(table = "NODE_DOCUMENT", name = "ENABLE_COMMENT_FORM", nullable = false)
    private Boolean enableCommentForm = true;

    @Column(table = "NODE_DOCUMENT", name = "ENABLE_COMMENTS_ON_FEEDS", nullable = false)
    private Boolean enableCommentsOnFeeds = true;

    @OneToMany(mappedBy = "document", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    @org.hibernate.annotations.OrderBy(clause = "CREATED_ON desc")
    @org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
    private List<Comment> comments = new ArrayList<Comment>();

    @Column(table = "NODE_DOCUMENT", name = "MACROS", nullable = false)
    //@org.hibernate.search.annotations.Field(index = org.hibernate.search.annotations.Index.UN_TOKENIZED)
    //@Searchable(description = "Macro", type = SearchableType.PHRASE)
    @org.hibernate.annotations.Index(name = "IDX_DOCUMENT_MACROS")
    private String macros = "";

    public Document() {
        super("New Document");
        content = "Edit this text..."; // Don't know why the interactive preview doesn't work without content
    }

    public Document(String name) {
        super(name);
    }

    public Document(Document original, boolean copyLazyProperties) {
        super(original);
        if (copyLazyProperties) {
            this.content = original.content;
        }
        this.nameAsTitle = original.nameAsTitle;
        this.enableComments = original.enableComments;
        this.enableCommentForm = original.enableCommentForm;
        this.macros = original.macros;
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

    public Boolean getEnableCommentsOnFeeds() {
        return enableCommentsOnFeeds;
    }

    public void setEnableCommentsOnFeeds(Boolean enableCommentsOnFeeds) {
        this.enableCommentsOnFeeds = enableCommentsOnFeeds;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getMacros() {
        return macros;
    }

    public void setMacros(String macros) {
        this.macros = macros;
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

    public boolean macroPresent(String macro) {
        return getMacros().contains(macro);
    }
}
