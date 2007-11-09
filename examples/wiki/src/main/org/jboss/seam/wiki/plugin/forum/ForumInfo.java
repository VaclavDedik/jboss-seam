package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.Comment;

public class ForumInfo {

    private boolean unreadPostings = false;
    private long totalNumOfTopics;
    private long totalNumOfPosts;
    private ForumTopic lastTopic;
    private Comment lastComment;

    public ForumInfo(long totalNumOfTopics, long totalNumOfPosts) {
        this.totalNumOfTopics = totalNumOfTopics;
        this.totalNumOfPosts = totalNumOfPosts;
    }

    public boolean isUnreadPostings() {
        return unreadPostings;
    }

    public void setUnreadPostings(boolean unreadPostings) {
        this.unreadPostings = unreadPostings;
    }

    public long getTotalNumOfTopics() {
        return totalNumOfTopics;
    }

    public void setTotalNumOfTopics(long totalNumOfTopics) {
        this.totalNumOfTopics = totalNumOfTopics;
    }

    public long getTotalNumOfPosts() {
        return totalNumOfPosts;
    }

    public void setTotalNumOfPosts(long totalNumOfPosts) {
        this.totalNumOfPosts = totalNumOfPosts;
    }

    public ForumTopic getLastTopic() {
        return lastTopic;
    }

    public void setLastTopic(ForumTopic lastTopic) {
        this.lastTopic = lastTopic;
    }

    public Comment getLastComment() {
        return lastComment;
    }

    public void setLastComment(Comment lastComment) {
        this.lastComment = lastComment;
    }

    // Was the last post made a topic or a comment/reply
    public boolean isLastPostLastTopic() {
        if (lastComment == null && lastTopic != null) return true;
        if (lastTopic != null && (lastTopic.getCreatedOn().getTime()>lastComment.getCreatedOn().getTime()) ) return true;
        return false;
    }

}
