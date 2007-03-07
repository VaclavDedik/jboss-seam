package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends Node {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEFAULT_DOCUMENT_ID", nullable = true)
    private Document defaultDocument;

    public Directory() { super("New Directory"); }

    public Directory(String name) {
        super(name);
    }

    // Mutable properties

    public Document getDefaultDocument() {
        return defaultDocument;
    }

    public void setDefaultDocument(Document defaultDocument) {
        this.defaultDocument = defaultDocument;
        makeDirty();
    }

    public String toString() {
        return getName();
    }

    public Directory getParent() {
        return (Directory)super.getParent();
    }
}
