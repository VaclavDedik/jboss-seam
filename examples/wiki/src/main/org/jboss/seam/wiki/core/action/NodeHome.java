/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.UserRoleAccessFactory;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Role;
import org.jboss.seam.wiki.util.WikiUtil;
import org.jboss.seam.wiki.preferences.PreferenceProvider;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.Component;
import org.jboss.seam.core.Events;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;

import java.util.Date;
import java.util.List;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends Node> extends EntityHome<N> {

    /* -------------------------- Context Wiring ------------------------------ */

    @In
    private NodeDAO nodeDAO;
    @In
    private UserDAO userDAO;
    @In
    protected User currentUser;
    @In
    protected List<Role.AccessLevel> accessLevelsList;

    protected NodeDAO getNodeDAO() { return nodeDAO; }
    protected UserDAO getUserDAO() { return userDAO; }
    protected User getCurrentUser() { return currentUser; }
    public List<Role.AccessLevel> getAccessLevelsList() { return accessLevelsList; }

    /* -------------------------- Request Wiring ------------------------------ */

    private Long parentDirectoryId;
    public Long getParentDirectoryId() {
        return parentDirectoryId;
    }
    public void setParentDirectoryId(Long parentDirectoryId) {
        this.parentDirectoryId = parentDirectoryId;
    }

    private Directory parentDirectory;
    public Directory getParentDirectory() {
        return parentDirectory;
    }
    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    public void setNodeId(Long o) {
        super.setId(o);
    }

    public Long getNodeId() {
        return (Long)super.getId();
    }

    public String init() {

        getLog().debug("initializing node home");

        // Load the parent instance
        if (!isIdDefined() && parentDirectoryId == null) {
            return "missingParameters";
        }

        if (!isIdDefined()) {
            getLog().debug("no instance identifier, getting parent directory with id: " + parentDirectoryId);
            parentDirectory = loadParentDirectory(parentDirectoryId);
        } else {
            getLog().debug("using parent of instance: " + getInstance());
            parentDirectory = getInstance().getParent();
            if (parentDirectory != null) // Wiki Root doesn't have a parent
                parentDirectoryId = parentDirectory.getId();
        }

        getLog().debug("initalized with parent directory: " + parentDirectory);

        // Outjects current node or parent directory, e.g. for breadcrumb rendering
        // TODO: This clashes if several subclasses of NodeHome run on the same page, e.g. DocumentHome + ForumHome
        Contexts.getPageContext().set("currentLocation", !isManaged() ? getParentDirectory() : getInstance());

        return null;
    }

    /* -------------------------- Basic Overrides ------------------------------ */


    @Override
    protected String getPersistenceContextName() {
        return "restrictedEntityManager";
    }

    // Access level filtered DAO for retrieval by identifier
    @Override
    public N find() {
        //noinspection unchecked
        N result = (N)nodeDAO.findNode((Long)getId());
        if (result==null) {
            handleNotFound();
        } else {
            writeAccessLevel = getAccessLevelsList().get(
                accessLevelsList.indexOf(
                    new Role.AccessLevel(result.getWriteAccessLevel())
                )
            );
            readAccessLevel = getAccessLevelsList().get(
                accessLevelsList.indexOf(
                    new Role.AccessLevel(result.getReadAccessLevel())
                )
            );
        }
        return result;
    }

    @Override
    protected N createInstance() {
        N node = super.createInstance();
        if (parentDirectory == null) {
            throw new IllegalStateException("Call the init() method before you use NodeHome");
        }
        // Set default permissions for new nodes - default to same access as parent directory
        node.setWriteAccessLevel(parentDirectory.getWriteAccessLevel());
        node.setReadAccessLevel(parentDirectory.getReadAccessLevel());
        writeAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentDirectory.getWriteAccessLevel())
            )
        );
        readAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentDirectory.getReadAccessLevel())
            )
        );

        return node;
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {
        checkPersistPermissions();

        if (!preparePersist()) return null;

        // Link the node with its parent directory
        getLog().trace("linking new node with its parent directory");
        parentDirectory.addChild(getInstance());

        // Last modified metadata
        setLastModifiedMetadata();

        // Wiki name conversion
        setWikiName();

        // Set created by user
        getLog().trace("setting created by user: " + getCurrentUser());
        getInstance().setCreatedBy(getCurrentUser());

        // Set its area number (if subclass didn't already set it)
        if (getInstance().getAreaNumber() == null)
            getInstance().setAreaNumber(getInstance().getParent().getAreaNumber());

        // Validate
        if (!isValidModel()) return null;

        if (!beforePersist()) return null;

        String outcome = super.persist();
        if (outcome != null) {
            Events.instance().raiseEvent("PreferenceEditor.flushAll");
        }
        return outcome;
    }

    @Override
    public String update() {
        checkUpdatePermissions();

        if (!prepareUpdate()) return null;

        // Last modified metadata
        setLastModifiedMetadata();

        // Wiki name conversion
        setWikiName();

        // Validate
        if (!isValidModel()) return null;

        if (!beforeUpdate()) return null;

        String outcome = super.update();
        if (outcome != null) {
            Events.instance().raiseEvent("PreferenceEditor.flushAll");
            Events.instance().raiseEvent("Nodes.menuStructureModified");
        }
        return outcome;
    }

    @Override
    public String remove() {
        checkRemovePermissions();

        if (!prepareRemove()) return null;

        // Unlink the node from its directory
        getInstance().getParent().removeChild(getInstance());

        if (!beforeRemove()) return null;

        // Delete preferences of this node
        PreferenceProvider provider = (PreferenceProvider) Component.getInstance("preferenceProvider");
        provider.deleteInstancePreferences(getInstance());

        String outcome = super.remove();
        if (outcome != null) {
            Events.instance().raiseEvent("Nodes.menuStructureModified");
        }
        return outcome;
    }

    protected boolean isValidModel() {
        getLog().trace("validating model");
        if (getParentDirectory() == null) return true; // Special case, editing the wiki root

        // Unique wiki name
        if (nodeDAO.isUniqueWikiname(getInstance())) {
            return true;
        } else {
            getFacesMessages().addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                "lacewiki.entity.DuplicateName",
                "This name is already used, please change it"
            );
            return false;
        }

    }

    /* -------------------------- Internal Methods ------------------------------ */

    protected void setWikiName() {
        getLog().trace("setting wiki name of node");
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));
    }

    protected void setLastModifiedMetadata() {
        getLog().trace("setting last modified metadata");
        getInstance().setLastModifiedBy(currentUser);
        getInstance().setLastModifiedOn(new Date());
    }

    protected void checkPersistPermissions() {
        getLog().trace("checking persist permissions");
        if (!Identity.instance().hasPermission("Node", "create", getParentDirectory()) )
            throw new AuthorizationException("You don't have permission for this operation");
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void checkUpdatePermissions() {
        getLog().trace("checking update permissions");
        if (!Identity.instance().hasPermission("Node", "edit", getInstance()) )
            throw new AuthorizationException("You don't have permission for this operation");
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected void checkRemovePermissions() {
        getLog().trace("checking remove permissions");
        if (!Identity.instance().hasPermission("Node", "edit", getInstance()) )
            throw new AuthorizationException("You don't have permission for this operation");
    }

    protected Directory loadParentDirectory(Long parentDirectoryId) {
        return nodeDAO.findDirectory(parentDirectoryId);        
    }

    /* -------------------------- Subclass Callbacks ------------------------------ */

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue processing
     */
    protected boolean preparePersist() { return true; }

    /**
     * Called after superclass did its preparation right before the actual persist()
     * @return boolean continue processing
     */
    protected boolean beforePersist() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue processing
     */
    protected boolean prepareUpdate() { return true; }

    /**
     * Called after superclass did its preparation right before the actual update()
     * @return boolean continue processing
     */
    protected boolean beforeUpdate() { return true; }

    /**
     * Called before the superclass does its preparation;
     * @return boolean continue processing
     */
    protected boolean prepareRemove() { return true; }

    /**
     * Called after superclass did its preparation right before the actual remove()
     * @return boolean continue processing
     */
    protected boolean beforeRemove() { return true; }

    /**
     * Called after the node has been disconnected from the old parent and reconnected to the new.
     * @param oldParent the previous parent directory
     * @param newParent the new parent directory
     */
    protected void afterNodeMoved(Directory oldParent, Directory newParent) {}

    /* -------------------------- Public Features ------------------------------ */

    /* Moving of nodes in the tree is not supported right now
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
            getInstance().setParent(parentDirectory); // TODO: Disconnects from old parent?
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());

            afterNodeMoved(oldParentDirectory, parentDirectory);
        }
    }
    */

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public void selectOwner(Long creatorId) {
        User newCreator = userDAO.findUser(creatorId);
        getInstance().setCreatedBy(newCreator);
    }

    private Role.AccessLevel writeAccessLevel;
    private Role.AccessLevel readAccessLevel;

    public Role.AccessLevel getWriteAccessLevel() {
        return writeAccessLevel;
    }

    public void setWriteAccessLevel(Role.AccessLevel writeAccessLevel) {
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
        this.writeAccessLevel = writeAccessLevel;
        getInstance().setWriteAccessLevel(
            writeAccessLevel != null ? writeAccessLevel.getAccessLevel() : UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL
        );
    }

    public Role.AccessLevel getReadAccessLevel() {
        return readAccessLevel;
    }

    public void setReadAccessLevel(Role.AccessLevel readAccessLevel) {
        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
        this.readAccessLevel = readAccessLevel;
        getInstance().setReadAccessLevel(
            readAccessLevel != null ? readAccessLevel.getAccessLevel() : UserRoleAccessFactory.ADMINROLE_ACCESSLEVEL
        );
    }

}
