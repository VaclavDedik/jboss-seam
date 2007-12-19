/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.wiki.util.WikiUtil;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import java.util.Date;
import java.util.List;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends WikiNode, P extends WikiNode> extends EntityHome<N> {

    /* -------------------------- Context Wiring ------------------------------ */

    @In
    private WikiNodeDAO wikiNodeDAO;
    @In
    private UserDAO userDAO;
    @In
    private TagDAO tagDAO;
    @In
    private WikiDirectory wikiRoot;
    @In
    protected User currentUser;
    @In
    protected List<Role.AccessLevel> accessLevelsList;

    public WikiNodeDAO getWikiNodeDAO() { return wikiNodeDAO; }
    public UserDAO getUserDAO() { return userDAO; }
    public TagDAO getTagDAO() { return tagDAO; }
    public WikiDirectory getWikiRoot() { return wikiRoot; }
    public User getCurrentUser() { return currentUser; }
    public List<Role.AccessLevel> getAccessLevelsList() { return accessLevelsList; }

    /* -------------------------- Request Wiring ------------------------------ */

    private Long parentNodeId;

    public Long getParentNodeId() {
        return parentNodeId;
    }
    public void setParentNodeId(Long parentNodeId) {
        this.parentNodeId = parentNodeId;
    }

    private P parentNode;
    public P getParentNode() {
        return parentNode;
    }
    public void setParentNode(P parentNode) {
        this.parentNode = parentNode;
    }

    public void setNodeId(Long o) {
        super.setId(o);
    }
    public Long getNodeId() {
        return (Long)super.getId();
    }

    /* -------------------------- Additional States ------------------------------ */

    private boolean edit = false;

    public boolean isEdit() { return edit; }
    public void setEdit(boolean edit) { this.edit = edit; }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected String getPersistenceContextName() {
        return "restrictedEntityManager";
    }

    @Override
    public N find() {
        getLog().debug("finding an existing instance with id: " + getId());
        N foundNode = findInstance();
        if (foundNode == null) {
            handleNotFound();
            return null;
        }
        getLog().debug("found instance: " + foundNode);
        return isEdit() ? beforeNodeEditFound(afterNodeFound(foundNode)) : afterNodeFound(foundNode);
    }

    @Override
    protected N createInstance() {
        getLog().debug("creating a new instance");
        N newNode = super.createInstance();
        getLog().debug("created new instance: " + newNode);
        return isEdit() ? beforeNodeEditNew(afterNodeCreated(newNode)) : afterNodeCreated(newNode);
    }

    /* -------------------------- Basic Subclass Callbacks ------------------------------ */

    public N afterNodeCreated(N node) {

        outjectCurrentLocation(node);

        return node;
    }

    public N beforeNodeEditNew(N node) {

        if (parentNodeId == null)
            throw new IllegalStateException("Missing parentNodeId parameter");

        getLog().debug("loading parent node with id: " + parentNodeId);
        parentNode = findParentNode(parentNodeId);
        if (parentNode == null)
            throw new IllegalStateException("Could not find parent node with id: " + parentNodeId);
        getLog().debug("initalized with parent node: " + parentNode);

        // Default to same access permissions as parent node
        node.setWriteAccessLevel(parentNode.getWriteAccessLevel());
        node.setReadAccessLevel(parentNode.getReadAccessLevel());
        writeAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentNode.getWriteAccessLevel())
            )
        );
        readAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(parentNode.getReadAccessLevel())
            )
        );

        return node;
    }

    public N afterNodeFound(N node) {

        getLog().debug("using parent of instance: " + node.getParent());
        if (node.getParent() != null) {  // Wiki Root doesn't have a parent
            parentNode = (P)node.getParent();
            parentNodeId = parentNode.getId();
        }

        outjectCurrentLocation(node);

        return node;
    }

    public N beforeNodeEditFound(N node) {

        writeAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(node.getWriteAccessLevel())
            )
        );
        readAccessLevel = getAccessLevelsList().get(
            accessLevelsList.indexOf(
                new Role.AccessLevel(node.getReadAccessLevel())
            )
        );

        return node;
    }


    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {
        checkPersistPermissions();

        if (!preparePersist()) return null;

        getLog().trace("linking new node with its parent node: " + getParentNode());
        getInstance().setParent(getParentNode());

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


    // TODO: Doesn't handle recursive deletion (only db cascading), so 2nd level cache and lucene index out of sync!
    @Override
    public String remove() {
        checkRemovePermissions();

        if (!prepareRemove()) return null;

        if (!beforeRemove()) return null;

        String outcome = super.remove();
        if (outcome != null) {
            Events.instance().raiseEvent("Nodes.menuStructureModified");
        }
        return outcome;
    }

    /* -------------------------- Internal (Subclass) Methods ------------------------------ */

    public abstract Class<N> getEntityClass();

    protected abstract N findInstance();

    protected abstract P findParentNode(Long parentNodeId);

    protected void outjectCurrentLocation(WikiNode node) {
        if (isPageRootController()) {
            // Outjects current node or parent directory, e.g. for breadcrumb rendering
            Contexts.getPageContext().set("currentLocation", node);
        }
    }

    protected void setWikiName() {
        getLog().trace("setting wiki name of node");
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));
    }

    protected void setLastModifiedMetadata() {
        getLog().trace("setting last modified metadata");
        getInstance().setLastModifiedBy(currentUser);
        getInstance().setLastModifiedOn(new Date());
    }

    protected boolean isValidModel() {
        getLog().trace("validating model");
        if (getParentNode() == null) return true; // Special case, editing the wiki root

        // Unique wiki name
        if (getWikiNodeDAO().isUniqueWikiname(getParentNode().getAreaNumber(), getInstance())) {
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

    protected void checkPersistPermissions() {
        getLog().trace("checking persist permissions");
        if (!Identity.instance().hasPermission("Node", "create", getParentNode()) )
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

    /* -------------------------- Optional Subclass Callbacks ------------------------------ */

    protected boolean isPageRootController() { return true; }

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
    protected void afterNodeMoved(WikiDirectory oldParent, WikiDirectory newParent) {}

    /* -------------------------- Public Features ------------------------------ */

    /* Moving of nodes in the tree is not supported right now
    public void parentDirectorySelected(NodeSelectedEvent nodeSelectedEvent) {
        // TODO: There is really no API in RichFaces to get the selection! Already shouted at devs...
        TreeRowKey rowkey = (TreeRowKey)((HtmlTree)nodeSelectedEvent.getSource()).getRowKey();
        Iterator pathIterator = rowkey.iterator();
        Long dirId = null;
        while (pathIterator.hasNext()) dirId = (Long)pathIterator.next();
        parentNode = nodeDAO.findDirectory(dirId);
        Directory oldParentDirectory = (Directory)getInstance().getParent();

        // Move node to different directory
        if (parentNode.getId() != oldParentDirectory.getId()) {

            // Null out default document of old parent
            removeAsDefaultDocument(oldParentDirectory);

            // Attach to new parent
            getInstance().setParent(parentNode); // TODO: Disconnects from old parent?
            getInstance().setAreaNumber(parentNode.getAreaNumber());

            afterNodeMoved(oldParentDirectory, parentNode);
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
            writeAccessLevel != null ? writeAccessLevel.getAccessLevel() : Role.ADMINROLE_ACCESSLEVEL
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
            readAccessLevel != null ? readAccessLevel.getAccessLevel() : Role.ADMINROLE_ACCESSLEVEL
        );
    }

    private List<DisplayTagCount> popularTags;

    public List<DisplayTagCount> getPopularTags() {
        // Load 6 most popular tags
        if (popularTags == null) popularTags = tagDAO.findTagCounts(getWikiRoot(), null, 6);
        return popularTags;
    }

}
