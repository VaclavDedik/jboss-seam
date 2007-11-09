package org.jboss.seam.wiki.plugin.forum;

import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Comment;
import org.jboss.seam.wiki.core.search.annotations.Searchable;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.persistence.*;

/**
 * Represents a forum topic (a particular thread of discussion).
 * <p>
 * We re-use the <tt>Document</tt> class and extend the node hierarchy further with
 * a new discriminator, <tt>FORUMTOPIC</tt>. In addition to normal document properties,
 * a forum topic has a few transient properties (sticky, unread, last comment, etc.) and
 * a few methods to handle macros that <i>must</i> appear before and after the topic
 * content (e.g. hideControls, hideCreatorHistory, forumReplies).
 * <p>
 * Although pragmatic, I'm not sure we'll handle macros that way (with string concatenation)
 * in the future. This should be made more generic and typesafe, I think we need macro metadata
 * - which really depends on how plugin metadata will be designed.
 *
 * @author Christian Bauer
 */
@Entity
@DiscriminatorValue("FORUMTOPIC")
@org.hibernate.search.annotations.Indexed
@Searchable(description = "Forum Topics")
public class ForumTopic extends Document {

    @Transient
    protected final String[] MACROS_BEFORE_CONTENT =
            {"clearBackground", "forumPosting"};
    @Transient
    protected final String[] MACROS_AFTER_CONTENT =
            {"forumReplies", "hideControls", "hideComments", "hideTags", "hideCreatorHistory"};

    @Transient
    private long numOfComments;
    @Transient
    private boolean sticky;
    @Transient
    private boolean unread;
    @Transient
    private Comment lastComment;

    public ForumTopic() {
        super("New Topic");
        setContentWithoutMacros("Edit this *wiki* text, the preview will update automatically.");
        setNameAsTitle(false);
        setEnableComments(true);
        setEnableCommentForm(true);
        setEnableCommentsOnFeeds(true);
    }

    public static ForumTopic fromArray(Object[] propertyValues) {
        ForumTopic topic = (ForumTopic)propertyValues[0];
        topic.setNumOfComments(propertyValues[1] != null ? (Long)propertyValues[1] : 0);
        topic.setSticky(propertyValues[2] != null ? ((Integer) propertyValues[2] != 0) : false);
        topic.setLastComment(propertyValues[3] != null ? (Comment)propertyValues[3] : null);
        return topic;
    }

    public long getNumOfComments() {
        return numOfComments;
    }

    public void setNumOfComments(long numOfComments) {
        this.numOfComments = numOfComments;
    }

    public boolean isSticky() {
        return sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    public Comment getLastComment() {
        return lastComment;
    }

    public void setLastComment(Comment lastComment) {
        this.lastComment = lastComment;
    }

    public String getContentWithoutMacros() {
        String content = getContent();
        // Cut from first double newline to last double newline
        return  content.substring( content.indexOf("\n\n")+2, content.lastIndexOf("\n\n") );
    }

    public void setContentWithoutMacros(String content) {
        // First, remove any macros that the user might have put into the text
        content = WikiUtil.removeMacros(content);
        
        // Apply the macros before and after content, separated with double newlines
        StringBuilder contentWithMacros = new StringBuilder();
        for (String s : MACROS_BEFORE_CONTENT) contentWithMacros.append("[<=").append(s).append("]\n");
        contentWithMacros.append("\n").append(content).append("\n\n");
        for (String s : MACROS_AFTER_CONTENT) contentWithMacros.append("[<=").append(s).append("]\n");
        if (isSticky()) contentWithMacros.append("[<=forumStickyPosting]").append("]\n");
        setContent(contentWithMacros.toString());
    }

    public void setDefaultMacros() {
        StringBuilder macros = new StringBuilder();
        for (String s : MACROS_BEFORE_CONTENT) macros.append(s).append(" ");
        for (String s : MACROS_AFTER_CONTENT) macros.append(s).append(" ");
        if (isSticky()) macros.append("forumStickyPosting").append(" ");
        setMacros(macros.substring(0, macros.length()-1));
    }

    public String getIconName() {
        StringBuilder iconName = new StringBuilder();
        iconName.append("posting");
        if (isSticky()) {
            iconName.append("_sticky");
        } else if (!getEnableCommentForm()) {
            iconName.append("_locked");
        }
        if (isUnread()) iconName.append("_unread");

        return iconName.toString();
    }

}
