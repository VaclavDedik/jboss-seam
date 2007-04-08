package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Feed;

import javax.faces.application.FacesMessage;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<Directory> {

    /* -------------------------- Context Wiring ------------------------------ */

    @DataModel
    List<Node> childNodes;

    @DataModelSelection
    Node selectedChildNode;

    /* -------------------------- Internal State ------------------------------ */

    private List<Document> childDocuments = new ArrayList<Document>();
    public List<Document> getChildDocuments() { return childDocuments; }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public void create() {
        super.create();

        // Fill the datamodel for outjection
        refreshChildNodes();

        // Feed checkbox
        hasFeed = getInstance().getFeed()!=null;
    }

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
        getNodeDAO().removeChildNodes(getInstance());
        return true;
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void refreshChildNodes() {
        childNodes = getInstance().getChildren();
        for (Node childNode : childNodes) {
            if (childNode instanceof Document) childDocuments.add((Document)childNode);
        }
    }

    @Transactional
    private void createOrRemoveFeed() {
        if (hasFeed && getInstance().getFeed() == null) {
            Feed feed = new Feed();
            feed.setDirectory(getInstance());
            feed.setAuthor(getInstance().getCreatedBy().getFullname());
            feed.setTitle(getInstance().getName());
            getInstance().setFeed(feed);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedCreated",
                "Created syndication feed for this directory");

        } else if (!hasFeed && getInstance().getFeed() != null) {
            getEntityManager().joinTransaction();
            getEntityManager().remove(getInstance().getFeed());
            getInstance().setFeed(null);

            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "feedRemoved",
                "Removed syndication feed of this directory");
        } else if (getInstance().getFeed() != null) {
            getInstance().getFeed().setTitle(getInstance().getName());
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveNodeUpInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position-1, position+1), 1);
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveNodeDownInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position, position+2), 1);
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void previewMenuItems() {
        refreshMenuItems();
    }

    private boolean hasFeed;

    public boolean isHasFeed() {
        return hasFeed;
    }

    public void setHasFeed(boolean hasFeed) {
        this.hasFeed = hasFeed;
    }

}
