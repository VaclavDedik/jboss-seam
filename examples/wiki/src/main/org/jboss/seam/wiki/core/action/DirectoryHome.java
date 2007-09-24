/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.core.dao.WikiTreeNodeAdapter;
import org.jboss.seam.wiki.util.WikiUtil;
import org.richfaces.model.TreeNode;

import javax.faces.application.FacesMessage;
import java.util.*;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<Directory> {

    /* -------------------------- Context Wiring ------------------------------ */


    /* -------------------------- Request Wiring ------------------------------ */

    @Observer("DirectoryHome.init")
    public String init() {
        String result = super.init();
        if (result != null) return result;

        // Fill the datamodel for outjection
        refreshChildNodes();

        // Feed checkbox
        hasFeed = getInstance().getFeed()!=null;

        return null;
    }

    /* -------------------------- Internal State ------------------------------ */

    private List<Document> childDocuments = new ArrayList<Document>();
    public List<Document> getChildDocuments() { return childDocuments; }


    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {

        if (getParentDirectory().getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(getParentDirectory().getAreaNumber());
            return super.persist();
        } else {
            // This is a logical area in the wiki root

            // Satisfy NOT NULL constraint
            getInstance().setAreaNumber(Long.MAX_VALUE);

            // Do the persist() first, we need the identifier after this
            String outcome = super.persist();

            getInstance().setAreaNumber(getInstance().getId());

            // And flush() again...
            getEntityManager().flush();
            return outcome;
        }
    }

    protected boolean beforePersist() {
        createOrRemoveFeed();
        return super.preparePersist();
    }

    protected boolean beforeUpdate() {
        createOrRemoveFeed();
        return super.beforeUpdate();
    }

    protected boolean prepareRemove() {
        if (getInstance().getParent() == null) return false; // Veto wiki root delete
        return true;
    }

    protected boolean beforeRemove() {
        // Remove all children (nested, recursively, udpates the second-level cache)
        getNodeDAO().removeChildren(getInstance());

        return true;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void refreshChildNodes() {
        childDocuments.clear();
        for (Node childNode : getInstance().getChildren()) {
            if (childNode instanceof Document) childDocuments.add((Document)childNode);
        }
    }

    private void createOrRemoveFeed() {
        if (hasFeed && getInstance().getFeed() == null) {
            // Does not have a feed but user wants one, create it
            Feed feed = new Feed();
            feed.setDirectory(getInstance());
            feed.setAuthor(getInstance().getCreatedBy().getFullname());
            feed.setTitle(getInstance().getName());
            feed.setDescription(getInstance().getDescription());
            getInstance().setFeed(feed);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedCreated",
                "Created syndication feed for this directory");

        } else if (!hasFeed && getInstance().getFeed() != null) {
            // Does have feed but user doesn't want it anymore... delete it
            getEntityManager().joinTransaction();
            getEntityManager().remove(getInstance().getFeed());
            getInstance().setFeed(null);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedRemoved",
                "Removed syndication feed of this directory");
        } else if (getInstance().getFeed() != null) {
            // Does have a feed and user still wants it, update the feed
            getInstance().getFeed().setTitle(getInstance().getName());
            getInstance().getFeed().setAuthor(getInstance().getCreatedBy().getFullname());
            getInstance().getFeed().setDescription(getInstance().getDescription());
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    @In(required=false)
    @Out(required = false, scope=ScopeType.PAGE)
    WikiTreeNodeAdapter directoryTree;

    public TreeNode getTree() {
        if (directoryTree == null) {
            directoryTree = new WikiTreeNodeAdapter(getInstance(), getNodeDAO().getComparatorDisplayPosition(), 2l);
            directoryTree.loadChildren();
        }
        return directoryTree;
        /*
        TreeNode root = new TreeNodeImpl();
        TreeNode bar = new TreeNodeImpl();
        TreeNode baz = new TreeNodeImpl();
        TreeNode faz  = new TreeNodeImpl();
        root.setData("Foo");
        bar.setData("bar");
        baz.setData("baz");
        faz.setData("faz");
        root.addChild("1", bar);
        root.addChild("2", baz);
        root.addChild("3", faz);
        return root;
        */
    }

    private boolean hasFeed;

    public boolean isHasFeed() {
        return hasFeed;
    }

    public void setHasFeed(boolean hasFeed) {
        this.hasFeed = hasFeed;
    }

    public void resetFeed() {
        if (getInstance().getFeed() != null) {
            getLog().debug("resetting feed of directory");
            getInstance().getFeed().getFeedEntries().clear();
            getInstance().getFeed().setPublishedDate(new Date());
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedReset",
                "Queued removal of all feed entries from the syndication feed of this directory, please update to finalize");
        }
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveNode(int currentPosition, int newPosition) {

        if (currentPosition != newPosition) {

            // Shift and refresh displayed list
            WikiUtil.shiftListElement(getInstance().getChildren(), currentPosition, newPosition);

            // Required update, this is only refreshed on database load
            for (Node node : getInstance().getChildren()) {
                node.setDisplayPosition( getInstance().getChildren().indexOf(node) );
            }

            refreshChildNodes();
        }
    }

}
