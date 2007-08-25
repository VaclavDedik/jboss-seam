/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import javax.persistence.*;

@Entity
@DiscriminatorValue("DIRECTORY")
@SecondaryTable(
    name = "NODE_DIRECTORY",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "DIRECTORY_ID")
)
@org.hibernate.annotations.Table(
    appliesTo = "NODE_DIRECTORY",
    foreignKey = @org.hibernate.annotations.ForeignKey(name = "FK_NODE_DIRECTORY_DIRECTORY_ID")
)
public class Directory extends Node {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(table = "NODE_DIRECTORY", name = "DEFAULT_DOCUMENT_ID", nullable = true)
    @org.hibernate.annotations.ForeignKey(name = "FK_DIRECTORY_DEFAULT_DOCUMENT_ID")
    private Document defaultDocument;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "directory", cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.LazyToOne(org.hibernate.annotations.LazyToOneOption.NO_PROXY)
    private Feed feed;

    public Directory() { super("New Directory"); }

    public Directory(String name) {
        super(name);
    }

    public Directory(Directory original) {
        super(original);
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

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }
}
