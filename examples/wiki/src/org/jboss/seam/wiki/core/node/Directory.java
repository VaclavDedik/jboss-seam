package org.jboss.seam.wiki.core.node;


import javax.persistence.*;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends Node {

    @ManyToOne(fetch = FetchType.EAGER) // Lazy would break UI logic that relies on classnames (proxy doesn't work)
    @JoinColumn(name = "DEFAULT_DOCUMENT_ID", nullable = true)
    private Document defaultDocument;

    public Directory() { super(); }

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
