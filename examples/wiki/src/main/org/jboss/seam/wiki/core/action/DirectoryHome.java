/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.international.Messages;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import java.util.*;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<WikiDirectory, WikiDirectory> {


    /* -------------------------- Context Wiring ------------------------------ */

    @In
    protected FeedDAO feedDAO;

    @In
    protected Clipboard clipboard;

    @In
    protected Pager pager;

    /* -------------------------- Internal State ------------------------------ */

    private boolean hasFeed;

    @DataModel(value = "childNodesList", scope = ScopeType.PAGE)
    private List<WikiNode> childNodes;

    private Map<WikiNode, Boolean> selectedNodes = new HashMap<WikiNode,Boolean>();

    private List<WikiDocument> childDocuments = new ArrayList<WikiDocument>();
    private List<WikiMenuItem> menuItems = new ArrayList<WikiMenuItem>();
    private SortedSet<WikiDirectory> alreadyUsedMenuItems = new TreeSet<WikiDirectory>();
    private SortedSet<WikiDirectory> availableMenuItems = new TreeSet<WikiDirectory>();
    private WikiDirectory selectedChildDirectory;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public Class<WikiDirectory> getEntityClass() {
        return WikiDirectory.class;
    }

    @Override
    public WikiDirectory findInstance() {
        return getWikiNodeDAO().findWikiDirectory((Long)getId());
    }

    @Override
    protected WikiDirectory findParentNode(Long parentNodeId) {
        return getEntityManager().find(WikiDirectory.class, parentNodeId);
    }

    @Override
    public WikiDirectory afterNodeFound(WikiDirectory dir) {
        super.afterNodeFound(dir);

        // Hm, not pretty but we can't have a @Factory here or Seam
        // complains that subclass has duplicate factory
        if (!Contexts.getPageContext().isSet("childNodesList")) {
            getLog().debug("refreshing child nodes after node found");
            refreshChildNodes(dir);
        }

        return dir;
    }

    @Override
    public WikiDirectory beforeNodeEditFound(WikiDirectory dir) {
        dir = super.beforeNodeEditFound(dir);

        hasFeed = dir.getFeed()!=null;

        childDocuments = getWikiNodeDAO().findWikiDocuments(dir);

        menuItems = getWikiNodeDAO().findMenuItems(dir);
        alreadyUsedMenuItems = new TreeSet<WikiDirectory>();
        for (WikiMenuItem menuItem : menuItems) {
            alreadyUsedMenuItems.add(menuItem.getDirectory());
        }
        refreshAvailableMenuItems(dir);

        return dir;
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {

        if (getParentNode().getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(getParentNode().getAreaNumber());
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

    @Override
    protected boolean beforePersist() {
        createOrRemoveFeed();
        return super.preparePersist();
    }

    @Override
    protected boolean beforeUpdate() {
        createOrRemoveFeed();
        updateMenuItems();
        return super.beforeUpdate();
    }

    @Override
    protected boolean prepareRemove() {
        // Wiki ROOT is special
        return getInstance().getParent() != null;
    }

    /* -------------------------- Messages ------------------------------ */

    @Override
    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Directory.Persist",
                "Directory '{0}' has been saved.",
                getInstance().getName()
        );
    }

    @Override
    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Directory.Update",
                "Directory '{0}' has been updated.",
                getInstance().getName()
        );
    }

    @Override
    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "lacewiki.msg.Directory.Delete",
                "Directory '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    protected void feedCreatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "lacewiki.msg.Feed.Create",
            "Created syndication feed for this directory");
    }

    protected void feedRemovedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "lacewiki.msg.Feed.Remove",
            "Removed syndication feed of this directory");
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void refreshChildNodes(WikiDirectory dir) {
        getLog().debug("refreshing child nodes of directory: " + dir);
        pager.setNumOfRecords(getWikiNodeDAO().findChildrenCount(dir));
        getLog().debug("number of children: " + pager.getNumOfRecords());
        if (pager.getNumOfRecords() > 0) {
            getLog().debug("loading children page from: " + pager.getNextRecord() + " size: " + pager.getPageSize());
            childNodes =
                    getWikiNodeDAO().findChildren(
                            dir, "createdOn", false,
                            new Long(pager.getNextRecord()).intValue(),
                            new Long(pager.getPageSize()).intValue()
                    );
        }
    }

    private void refreshAvailableMenuItems(WikiDirectory dir) {
        availableMenuItems = new TreeSet();
        availableMenuItems.addAll(getWikiNodeDAO().findChildWikiDirectories(dir));
        availableMenuItems.removeAll(alreadyUsedMenuItems);
    }

    private void updateMenuItems() {
        if ( Identity.instance().hasPermission("Node", "editMenu", getInstance()) ) {
            // No point in doing that if the user couldn't have edited anything

            // Compare the edited list of menu items to the persistent menu items and insert/remove accordingly
            List<WikiMenuItem> persistentMenuItems = getWikiNodeDAO().findMenuItems(getInstance());
            for (WikiMenuItem persistentMenuItem : persistentMenuItems) {
                if (menuItems.contains(persistentMenuItem)) {
                    persistentMenuItem.setDisplayPosition(menuItems.indexOf(persistentMenuItem));
                    getLog().debug("Updated menu: " + persistentMenuItem);
                } else {
                    getEntityManager().remove(persistentMenuItem);
                    getLog().debug("Removed menu: " + persistentMenuItem);
                }
            }
            for (WikiMenuItem menuItem : menuItems) {
                if (!persistentMenuItems.contains(menuItem)) {
                    menuItem.setDisplayPosition(menuItems.indexOf(menuItem));
                    getEntityManager().persist(menuItem);
                    getLog().debug("Inserted menu: " + menuItem);
                }
            }
        }
    }

    public void createOrRemoveFeed() {
        if (hasFeed && getInstance().getFeed() == null) {
            // Does not have a feed but user wants one, create it
            feedDAO.createFeed(getInstance());
            feedCreatedMessage();

        } else if (!hasFeed && getInstance().getFeed() != null) {
            // Does have feed but user doesn't want it anymore... delete it
            feedDAO.removeFeed(getInstance());
            feedRemovedMessage();

        } else if (getInstance().getFeed() != null) {
            // Does have a feed and user still wants it, update the feed
            feedDAO.updateFeed(getInstance());
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    @Observer(value = "PersistenceContext.filterReset", create = false)
    public void refreshChildNodes() {
        if (isManaged()) {
            getLog().debug("refreshing child nodes of the current instance");
            refreshChildNodes(getInstance());
        }
    }

    @RequestParameter
    public void setPage(Integer page) {
        pager.setPage(page);
    }

    public Pager getPager() {
        return pager;
    }

    public List<WikiNode> getChildNodes() { return childNodes; }

    public List<WikiDocument> getChildDocuments() { return childDocuments; }

    public List<WikiMenuItem> getMenuItems() { return menuItems; }

    public Map<WikiNode, Boolean> getSelectedNodes() { return selectedNodes; }

    public WikiDirectory getSelectedChildDirectory() { return selectedChildDirectory; }
    public void setSelectedChildDirectory(WikiDirectory selectedChildDirectory) { this.selectedChildDirectory = selectedChildDirectory; }

    public SortedSet<WikiDirectory> getAvailableMenuItems() { return availableMenuItems; }

    public boolean isHasFeed() { return hasFeed; }
    public void setHasFeed(boolean hasFeed) { this.hasFeed = hasFeed; }

    public void resetFeed() {
        if (getInstance().getFeed() != null) {
            getLog().debug("resetting feed of directory");
            getInstance().getFeed().getFeedEntries().clear();
            getInstance().getFeed().setPublishedDate(new Date());
            getFacesMessages().addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "lacewiki.msg.Feed.Reset",
                "Queued removal of all feed entries from the syndication feed of this directory, please update to finalize");
        }
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void removeMenuItem(Long menuItemId) {
        Iterator<WikiMenuItem> it = menuItems.iterator();
        while (it.hasNext()) {
            WikiMenuItem wikiMenuItem = it.next();
            if (wikiMenuItem.getDirectoryId().equals(menuItemId)) {
                getLog().debug("Removing menu item: " + menuItemId);
                it.remove();
                alreadyUsedMenuItems.remove(wikiMenuItem.getDirectory());
                refreshAvailableMenuItems(getInstance());
            }
        }
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void addMenuItem() {
        if (selectedChildDirectory != null) {
            getLog().debug("Adding menu item: " + selectedChildDirectory);
            WikiMenuItem newMenuItem = new WikiMenuItem(selectedChildDirectory);
            menuItems.add(newMenuItem);
            alreadyUsedMenuItems.add(selectedChildDirectory);
            refreshAvailableMenuItems(getInstance());
        }
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveMenuItem(int currentPosition, int newPosition) {

        if (currentPosition != newPosition) {
            // Shift and refresh displayed list
            WikiUtil.shiftListElement(menuItems, currentPosition, newPosition);
        }
    }

    // TODO: Most of this clipboard stuff is based on the hope that nobody modifies anything while we have it in the clipboard...

    public void clearClipboard() {
        clipboard.clear();
    }

    public void copy() {
        for (Map.Entry<WikiNode, Boolean> entry : getSelectedNodes().entrySet()) {
            if (entry.getValue()) {
                getLog().debug("copying to clipboard: " + entry.getKey());
                clipboard.add(entry.getKey(), false);
            }
        }
        selectedNodes.clear();
    }

    @Restrict("#{s:hasPermission('Node', 'edit', directoryHome.instance)}")
    public void cut() {
        for (Map.Entry<WikiNode, Boolean> entry : getSelectedNodes().entrySet()) {
            if (entry.getValue()) {
                getLog().debug("cutting to clipboard: " + entry.getKey());
                clipboard.add(entry.getKey(), true);
            }
        }
        selectedNodes.clear();
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'create', directoryHome.instance)}")
    public void paste() {

        if (getInstance().getId().equals(getWikiRoot().getId())) return; // Can't paste in wiki root

        // Batch the work
        int batchSize = 2;
        int i = 0;
        List<Long> batchIds = new ArrayList<Long>();
        for (WikiNode clipboardNode : clipboard.getItems()) {
            i++;
            batchIds.add(clipboardNode.getId());
            if (i % batchSize == 0) {
                List<WikiNode> nodesForPasteBatch = getWikiNodeDAO().findWikiNodes(batchIds);
                pasteNodes(nodesForPasteBatch);
                batchIds.clear();
            }
        }
        // Last batch
        if (batchIds.size() != 0) {
            List<WikiNode> nodesForPasteBatch = getWikiNodeDAO().findWikiNodes(batchIds);
            pasteNodes(nodesForPasteBatch);
        }

        getLog().debug("completed executing paste, refreshing...");

        selectedNodes.clear();
        clipboard.clear();
        refreshChildNodes();
    }

    private void pasteNodes(List<WikiNode> nodes) {
        getLog().debug("executing paste batch");
        for (WikiNode n: nodes) {
            getLog().debug("pasting clipboard item: " + n);
            String pastedName = n.getName();

            // Check unique name if we are not cutting and pasting into the same area
            if (!(clipboard.isCut(n.getId()) && n.getParent().getAreaNumber().equals(getInstance().getAreaNumber()))) {
                getLog().debug("pasting node into different area, checking wikiname");

                if (!getWikiNodeDAO().isUniqueWikiname(getInstance().getAreaNumber(), WikiUtil.convertToWikiName(pastedName))) {
                    getLog().debug("wikiname is not unique, renaming");
                    if (pastedName.length() > 245) {
                        getFacesMessages().addToControlFromResourceBundleOrDefault(
                            "name",
                            SEVERITY_ERROR,
                            "lacewiki.msg.Clipboard.DuplicatePasteNameFailure",
                            "The name '{0}' was already in use in this area and is too long to be renamed, skipping paste.",
                            pastedName
                        );
                        continue; // Jump to next loop iteration when we can't append a number to the name
                    }

                    // Now try to add "Copy 1", "Copy 2" etc. to the name until it is unique
                    int i = 1;
                    String attemptedName = pastedName + " " + Messages.instance().get("lacewiki.label.Clipboard.CopySuffix") + i;
                    while (!getWikiNodeDAO().isUniqueWikiname(getInstance().getAreaNumber(), WikiUtil.convertToWikiName(attemptedName))) {
                        attemptedName = pastedName + " " + Messages.instance().get("lacewiki.label.Clipboard.CopySuffix") + (++i);
                    }
                    pastedName = attemptedName;

                    getFacesMessages().addToControlFromResourceBundleOrDefault(
                        "name",
                        SEVERITY_INFO,
                        "lacewiki.msg.Clipboard.DuplicatePasteName",
                        "The name '{0}' was already in use in this area, renamed item to '{1}'.",
                        n.getName(), pastedName
                    );
                }
            }

            if (clipboard.isCut(n.getId())) {
                getLog().debug("cut pasting: " + n);

                // Check if the cut item was a default file for its parent
                if ( ((WikiDirectory)n.getParent()).getDefaultFile() != null &&
                    ((WikiDirectory)n.getParent()).getDefaultFile().getId().equals(n.getId())) {
                    getLog().debug("cutting default file of directory: " + n.getParent());
                    ((WikiDirectory)n.getParent()).setDefaultFile(null);
                }

                n.setName(pastedName);
                n.setWikiname(WikiUtil.convertToWikiName(pastedName));
                n.setParent(getInstance());

                // If we cut and paste into a new area, all children must be updated as well
                if (!getInstance().getAreaNumber().equals(n.getAreaNumber())) {
                    n.setAreaNumber(getInstance().getAreaNumber());

                    // TODO: Ugly and memory intensive, better use a database query but HQL updates are limited with joins
                    if (n.isInstance(WikiDocument.class)) {
                        List<WikiComment> comments = getWikiNodeDAO().findWikiCommentsFlat((WikiDocument)n, true);
                        for (WikiComment comment : comments) {
                            comment.setAreaNumber(n.getAreaNumber());
                        }
                    }
                }

            } else {
                getLog().debug("copy pasting: " + n);
                WikiNode newNode = n.duplicate(true);
                newNode.setName(pastedName);
                newNode.setWikiname(WikiUtil.convertToWikiName(pastedName));
                newNode.setParent(getInstance());
                newNode.setAreaNumber(getInstance().getAreaNumber());
                newNode.setCreatedBy(getUserDAO().findUser(n.getCreatedBy().getId()));
                if (n.getLastModifiedBy() != null) {
                    newNode.setLastModifiedBy(getUserDAO().findUser(n.getLastModifiedBy().getId()));
                }
                getEntityManager().persist(newNode);
            }
        }
        getLog().debug("completed executing of paste batch");
    }

}
