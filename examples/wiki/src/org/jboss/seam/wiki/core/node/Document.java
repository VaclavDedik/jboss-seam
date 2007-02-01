package org.jboss.seam.wiki.core.node;

import org.hibernate.validator.Length;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DOCUMENT")
public class Document extends Node {

    @Column(name = "CONTENT")
    @Length(min = 1, max = 32768)
    private String content;

    public Document() { super(); }

    public Document(String name) {
        super(name);
    }

    // Mutable properties

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
        makeDirty();
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
}
