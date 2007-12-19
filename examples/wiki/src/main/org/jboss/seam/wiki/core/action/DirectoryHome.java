/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.feeds.FeedDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiMenuItem;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import static javax.faces.application.FacesMessage.SEVERITY_INFO;
import java.util.*;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<WikiDirectory, WikiDirectory> {


    /* -------------------------- Context Wiring ------------------------------ */

    @In
    protected FeedDAO feedDAO;


    /* -------------------------- Internal State ------------------------------ */

    // TODO: Move page size into preferences
    private Pager pager = new Pager(15l);
    private boolean hasFeed;
    private List<WikiNode> childNodes = new ArrayList<WikiNode>();
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

        refreshChildNodes(dir);

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
        if (getInstance().getParent() == null) return false; // Veto wiki root delete
        return true;
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
        pager.setNumOfRecords(getWikiNodeDAO().findChildrenCount(dir));
        if (pager.getNumOfRecords() > 0) {
            childNodes = getWikiNodeDAO().findChildren(dir, "createdOn", false, pager.getNextRecord(), pager.getPageSize());
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
        if (isManaged()) refreshChildNodes(getInstance());
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

    public WikiDirectory getSelectedChildDirectory() { return selectedChildDirectory; }
    public void setSelectedChildDirectory(WikiDirectory selectedChildDirectory) { this.selectedChildDirectory = (WikiDirectory)selectedChildDirectory; }

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

}
