package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.wiki.core.model.Document;

import java.io.Serializable;

public class BlogEntry implements Serializable {

    Document entryDocument;
    Long commentCount;

    public BlogEntry(Document entryDocument) {
        this.entryDocument = entryDocument;
    }

    public BlogEntry(Document entryDocument, Long commentCount) {
        this.entryDocument = entryDocument;
        this.commentCount = commentCount;
    }

    public Document getEntryDocument() {
        return entryDocument;
    }

    public void setEntryDocument(Document entryDocument) {
        this.entryDocument = entryDocument;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }
}
