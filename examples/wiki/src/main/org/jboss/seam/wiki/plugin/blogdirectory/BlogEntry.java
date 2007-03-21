package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.wiki.core.model.Document;

import java.io.Serializable;

public class BlogEntry implements Serializable {

    Document entryDocument;

    public BlogEntry(Document entryDocument) {
        this.entryDocument = entryDocument;
    }

    public Document getEntryDocument() {
        return entryDocument;
    }

    public void setEntryDocument(Document entryDocument) {
        this.entryDocument = entryDocument;
    }
}
