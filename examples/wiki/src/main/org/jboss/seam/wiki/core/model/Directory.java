package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DIRECTORY")
public class Directory extends Node {

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "DEFAULT_DOCUMENT_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_DIRECTORY_DEFAULT_DOCUMENT_ID")
    private Document defaultDocument;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "directory", cascade = CascadeType.PERSIST)
    private Feed feed;

    public Directory() { super("New Directory"); }

    public Directory(String name) {
        super(name);
    }

    // Mutable properties

    /**
     * Careful calling this, it always returns the assigned Document, even if
     * the user has a lower access level. Hibernate filters don't filter many-to-one
     * because if we have the id, we get the instance.
     *
     * @return Document The assigned default starting document of this directory
     */
    public Document getDefaultDocument() {
        return defaultDocument;
    }

    public void setDefaultDocument(Document defaultDocument) {
        this.defaultDocument = defaultDocument;
    }

    public String toString() {
        return getName();
    }

    public Directory getParent() {
        return (Directory)super.getParent();
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }
}
