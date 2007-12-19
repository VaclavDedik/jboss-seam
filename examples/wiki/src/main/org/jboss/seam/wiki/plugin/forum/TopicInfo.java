package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiComment;

public class TopicInfo {

    private WikiDocument topic;
    private boolean unread;
    private boolean sticky;
    private long numOfReplies;
    private WikiComment lastComment;

    public TopicInfo(WikiDocument topic, Integer sticky) {
        this.topic = topic;
        this.sticky = sticky != 0;
    }

    public WikiDocument getTopic() {
        return topic;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public long getNumOfReplies() {
        return numOfReplies;
    }

    public void setNumOfReplies(long numOfReplies) {
        this.numOfReplies = numOfReplies;
    }

    public WikiComment getLastComment() {
        return lastComment;
    }

    public void setLastComment(WikiComment lastComment) {
        this.lastComment = lastComment;
    }

    public String getIconName() {
        StringBuilder iconName = new StringBuilder();
        iconName.append("posting");
        if (isSticky()) {
            iconName.append("_sticky");
        } else if (!topic.isEnableCommentForm()) {
            iconName.append("_locked");
        }
        if (isUnread()) iconName.append("_unread");

        return iconName.toString();
    }


    public String toString() {
        return "TopicInfo(" + getTopic().getId() +
                ") replies: " + getNumOfReplies() +
                ", unread: " + isUnread() +
                ", sticky: " + isSticky() +
                ", last comment: " + getLastComment();
    }

}
