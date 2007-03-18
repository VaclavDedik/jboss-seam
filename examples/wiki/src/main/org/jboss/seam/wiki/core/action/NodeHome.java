package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import javax.persistence.EntityManager;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.User;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.ui.WikiUtil;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.Component;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.security.Identity;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends Node> extends EntityHome<N> {

    // Convenience wiring for subclasses
    @In private NodeDAO nodeDAO;
    @In private UserDAO userDAO;
    @In private User currentUser;
    private Directory parentDirectory; // Assigned in create()

    protected NodeDAO getNodeDAO() { return nodeDAO; }
    protected UserDAO getUserDAO() { return userDAO; }
    protected User getCurrentUser() { return currentUser; }
    public Directory getParentDirectory() { return parentDirectory; }

    // 'Edit' request parameter
    @RequestParameter private Long nodeId;

    // 'Create' request parameter
    @RequestParameter private Long parentDirId;

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

    // Access level filtered DAO
    @Override
    public N find() {
        N result = (N)nodeDAO.findNode((Long)getId());
        if (result==null) handleNotFound();
        return result;
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

    @Override
    protected N createInstance() {
        N node = super.createInstance();

        // Set default permissions for new nodes - default to same access as parent directory
        node.setWriteAccessLevel(getParentDirectory().getWriteAccessLevel());
        node.setReadAccessLevel(getParentDirectory().getReadAccessLevel());

        return node;
    }

    @Override
    public String persist() {

        // Permission check (double check if subclass already called it)
        checkNodeAccessLevelChangePermission();

        // Set the wikiname
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));

        // Link the node with its parent directory
        getParentDirectory().addChild(getInstance());

        // Set created by user
        getInstance().setCreatedBy(getCurrentUser());

        // Set its area number (if subclass didn't already set it)
        if (getInstance().getAreaNumber() == null)
            getInstance().setAreaNumber(getParentDirectory().getAreaNumber());

        // Validate
        if (!isValidModel()) return null;

        return super.persist();
    }

    @Override
    public String update() {

        // Permission check (double check if subclass already called it)
        checkNodeAccessLevelChangePermission();

        // Set last modified by user
        getInstance().setLastModifiedBy(getCurrentUser());

        // Validate
        if (!isValidModel()) return null;

        // Refresh UI
        Events.instance().raiseEvent("Nodes.menuStructureModified");

        // Set the wikiname
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));

        return super.update();
    }

    @Override
    public String remove() {

        // Unlink the document from its directory
        getInstance().getParent().removeChild(getInstance());

        // Refresh UI
        Events.instance().raiseEvent("Nodes.menuStructureModified");

        return super.remove();
    }

    // TODO: Typical exit method to get out of a root or nested conversation, JBSEAM-906
    public void exitConversation(Boolean endBeforeRedirect) {
        Conversation currentConversation = Conversation.instance();
        if (currentConversation.isNested()) {
            // End this nested conversation and return to last rendered view-id of parent
            currentConversation.endAndRedirect(endBeforeRedirect);
        } else {
            // End this root conversation
            currentConversation.end();
            // Return to the view-id that was captured when this conversation started
            NodeBrowser browser = (NodeBrowser) Component.getInstance("browser");
            if (endBeforeRedirect)
                browser.redirectToLastBrowsedPage();
            else
                browser.redirectToLastBrowsedPageWithConversation();
        }
    }

    protected void checkNodeAccessLevelChangePermission() {

        if (!Identity.instance().hasPermission("Node", "changeAccessLevel", getInstance()))
            throw new AuthorizationException("You don't have permission for this operation");
    }

    // Validation rules for persist(), update(), and remove();

    private boolean isValidModel() {
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

}
