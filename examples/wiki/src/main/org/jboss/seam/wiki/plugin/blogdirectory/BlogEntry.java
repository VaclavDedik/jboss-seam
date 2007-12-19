package org.jboss.seam.wiki.plugin.blogdirectory;

import org.jboss.seam.wiki.core.model.WikiDocument;

import java.io.Serializable;

public class BlogEntry implements Serializable {

    WikiDocument entryDocument;
    Long commentCount;

    public BlogEntry() {}

    public BlogEntry(WikiDocument entryDocument) {
        this.entryDocument = entryDocument;
    }

    public BlogEntry(WikiDocument entryDocument, Long commentCount) {
        this.entryDocument = entryDocument;
        this.commentCount = commentCount;
    }

    public WikiDocument getEntryDocument() {
        return entryDocument;
    }

    public void setEntryDocument(WikiDocument entryDocument) {
        this.entryDocument = entryDocument;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public String toString() {
        return "BlogEntry: " + entryDocument + " Comments: " + commentCount;
    }
}
