package org.jboss.seam.wiki.core.model;

import org.hibernate.validator.Length;
import org.jboss.seam.annotations.security.Restrict;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DOCUMENT")
@Restrict
public class Document extends Node {

    @Column(name = "CONTENT")
    @Length(min = 1, max = 32768)
    private String content;

    @Column(name = "NAME_AS_TITLE")
    private Boolean nameAsTitle;

    public Document() { super("New Document"); }

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

    public Directory getParent() {
        return (Directory)super.getParent();
    }

    public void addChild(Node child) {
        throw new UnsupportedOperationException("Documents can't have children");
    }

    public void removeChild(Node child) {
        throw new UnsupportedOperationException("Documents can't have children");
    }

    public String toString() {
        return getName();
    }

    public void rollback(Node revision) {
        super.rollback(revision);
        this.content = ((Document)revision).content;
    }
}
