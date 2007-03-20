package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.ui.WikiUtil;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.core.Events;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.TreeRowKey;
import org.richfaces.component.events.NodeSelectedEvent;

import java.util.Date;
import java.util.Iterator;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends Node> extends EntityHome<N> {

    /* -------------------------- Context Wiring ------------------------------ */

    @In private NodeDAO nodeDAO;
    @In private UserDAO userDAO;
    @In private User currentUser;
    protected NodeDAO getNodeDAO() { return nodeDAO; }
    protected UserDAO getUserDAO() { return userDAO; }
    protected User getCurrentUser() { return currentUser; }

    @Override
    @Out(value = "currentNode", scope = ScopeType.CONVERSATION)
    public N getInstance() {
        return super.getInstance();
    }

    /* -------------------------- Request Wiring ------------------------------ */

    // Required 'Edit' request parameter
    @RequestParameter private Long nodeId;

    // Required 'Edit' and 'Create' request parameter
    @RequestParameter private Long parentDirId;

    /* -------------------------- Internal State ------------------------------ */

    private Directory parentDirectory;
    public Directory getParentDirectory() { return parentDirectory; }
    public void setParentDirectory(Directory parentDirectory) { this.parentDirectory = parentDirectory; }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected String getPersistenceContextName() {
        return "restrictedEntityManager";
    }

    // 'Edit' or 'Create'
    @Override
    public Object getId() {
        if (nodeId == null) {
            return super.getId();
        } else {
            return nodeId;
        }
    }

    // Access level filtered DAO for retrieval by identifier
    @Override
    public N find() {
        //noinspection unchecked
        N result = (N)nodeDAO.findNode((Long)getId());
        if (result==null) handleNotFound();
        return result;
    }

    @Override
    protected N createInstance() {
        N node = super.createInstance();

        // Set default permissions for new nodes - default to same access as parent directory
        node.setWriteAccessLevel(getParentDirectory().getWriteAccessLevel());
        node.setReadAccessLevel(getParentDirectory().getReadAccessLevel());

        return node;
    }

    @Override
    public void create() {
        super.create();

        // Load the parent directory (needs to be called first)
        // The parentDirectory (and parentDirId) parameter can actually be null but this only happens
        // when the wiki root is edited... it can only be update()ed anyway, all the other code is null-safe.
        parentDirectory = nodeDAO.findDirectory(parentDirId);

        // Permission checks
        if (!isManaged() && !Identity.instance().hasPermission("Node", "create", getParentDirectory()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        } else if ( !Identity.instance().hasPermission("Node", "edit", getInstance()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }

        // Outject current node
        Contexts.getConversationContext().set("currentNode", getInstance());
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {
        if (!preparePersist()) return null;

        // Permission checks
        checkNodeAccessLevelChangePermission();

        // Last modified metadata
        setLastModifiedMetadata();

        // Wiki name conversion
        setWikiName();

        // Link the node with its parent directory
        getParentDirectory().addChild(getInstance());

        // Set created by user
        getInstance().setCreatedBy(getCurrentUser());

        // Set its area number (if subclass didn't already set it)
        if (getInstance().getAreaNumber() == null)
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());

        // Validate
        if (!isValidModel()) return null;

        if (!beforePersist()) return null;
        return super.persist();
    }

    @Override
    public String update() {
        if (!prepareUpdate()) return null;

        // Permission checks
        checkNodeAccessLevelChangePermission();

        // Last modified metadata
        setLastModifiedMetadata();

        // Wiki name conversion
        setWikiName();

        // Refresh UI
        refreshMenuItems();

        // Validate
        if (!isValidModel()) return null;

        if (!beforeUpdate()) return null;
        return super.update();
    }

    @Override
    public String remove() {
        if (!prepareRemove()) return null;

        // Unlink the node from its directory
        getInstance().getParent().removeChild(getInstance());

        // Refresh UI
        refreshMenuItems();

        if (!beforeRemove()) return null;
        return super.remove();
    }

    public void parentDirectorySelected(NodeSelectedEvent nodeSelectedEvent) {
        // TODO: There is really no API in RichFaces to get the selection! Already shouted at devs...
        TreeRowKey rowkey = (TreeRowKey)((HtmlTree)nodeSelectedEvent.getSource()).getRowKey();
        Iterator pathIterator = rowkey.iterator();
        Long dirId = null;
        while (pathIterator.hasNext()) dirId = (Long)pathIterator.next();
        parentDirectory = nodeDAO.findDirectory(dirId);
        Directory oldParentDirectory = (Directory)getInstance().getParent();

        // Move node to different directory
        if (parentDirectory.getId() != oldParentDirectory.getId()) {

            // Null out default document of old parent
            removeAsDefaultDocument(oldParentDirectory);

            // Attach to new parent
            parentDirectory.addChild(getInstance()); // Disconnects from old parent
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());

            afterNodeMoved(oldParentDirectory, parentDirectory);
        }
    }

    protected boolean isValidModel() {
        if (getParentDirectory() == null) return true; // Special case, editing the wiki root

        // Unique wiki name
        if (nodeDAO.isUniqueWikiname(getInstance())) {
            return true;
        } else {
            getFacesMessages().addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateName",
                "This name is already used, please change it."
            );
            return false;
        }

    }

    /* -------------------------- Internal Methods ------------------------------ */

    protected void setWikiName() {
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));
    }

    protected void setLastModifiedMetadata() {
        getInstance().setLastModifiedBy(currentUser);
        getInstance().setLastModifiedOn(new Date());
    }

    protected void checkNodeAccessLevelChangePermission() {
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void removeAsDefaultDocument(Directory directory) {
        if (directory.getDefaultDocument() != null &&
            directory.getDefaultDocument().getId().equals(getInstance().getId())
           ) directory.setDefaultDocument(null);
    }

    protected void refreshMenuItems() {
        if (getInstance().isMenuItem())
            Events.instance().raiseEvent("Nodes.menuStructureModified");
    }

    /* -------------------------- Subclass Callbacks ------------------------------ */

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue or veto
     */
    protected boolean preparePersist() { return true; }

    /**
     * Called after superclass did its preparation right before the actual persist()
     * @return boolean continue or veto
     */
    protected boolean beforePersist() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue or veto
     */
    protected boolean prepareUpdate() { return true; }

    /**
     * Called after superclass did its preparation right before the actual update()
     * @return boolean continue or veto
     */
    protected boolean beforeUpdate() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue or veto
     */
    protected boolean prepareRemove() { return true; }

    /**
     * Called after superclass did its preparation right before the actual remove()
     * @return boolean continue or veto
     */
    protected boolean beforeRemove() { return true; }

    /**
     * Called after the node has been disconnected from the old parent and reconnected to the new.
     * @param oldParent the previous parent directory
     * @param newParent the new parent directory
     */
    protected void afterNodeMoved(Directory oldParent, Directory newParent) {}

}
