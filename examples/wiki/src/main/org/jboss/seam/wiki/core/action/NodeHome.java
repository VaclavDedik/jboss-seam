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
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.RequestParameter;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

/**
 * Superclass for all creating and editing documents, directories, files, etc.
 *
 * @author Christian Bauer
 */
public abstract class NodeHome<N extends Node> extends EntityHome<N> {

    // Convenience wiring for subclasses
    @In private NodeDAO nodeDAO;
    @In private UserDAO userDAO;
    @In private User authenticatedUser;

    protected Directory parentDirectory; // Assigned in create()

    protected NodeDAO getNodeDAO() { return nodeDAO; }
    protected UserDAO getUserDAO() { return userDAO; }
    protected User getAuthenticatedUser() { return authenticatedUser; }
    public Directory getParentDirectory() { return parentDirectory; }

    // 'Edit' request parameter
    @RequestParameter private Long nodeId;

    // 'Create' request parameter
    @RequestParameter private Long parentDirId;

    @Out(required = true, scope = ScopeType.CONVERSATION)
    protected N currentNode;

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

    // 'Edit' or 'Create'
    @Override
    public Object getId() {

        if (nodeId == null) {
            return super.getId();
        } else {
            return nodeId;
        }
    }

    @Override
    public void create() {
        super.create();

        // Load the parent directory (needs to be called first, ugly dependency in createInstance() )
        // The parentDirectory (and parentDirId) parameter can actually be null but this onl happens
        // when the wiki root is edited... it can only be update()ed anyway.
        parentDirectory = nodeDAO.findDirectory(parentDirId);

        // Outject current node
        currentNode = getInstance();
    }

    @Override
    protected N createInstance() {
        N node = super.createInstance();

        // Set default permissions for new nodes - just like parent directory
        node.setWriteAccessLevel(parentDirectory.getWriteAccessLevel());
        node.setReadAccessLevel(parentDirectory.getReadAccessLevel());

        return node;
    }

    @Override
    public String persist() {

        // Set the wikiname
        getInstance().setWikiname(WikiUtil.convertToWikiName(getInstance().getName()));

        // Link the document with a directory
        parentDirectory.addChild(getInstance());

        // Set created by user
        getInstance().setCreatedBy(authenticatedUser);

        // Set its area number
        if (getInstance().getAreaNumber() == null)
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());

        // Validate
        if (!isUniqueWikinameInDirectory() ||
            !isUniqueWikinameInArea()) return null;

        return super.persist();
    }

    @Override
    public String update() {

        // Set last modified by user
        getInstance().setLastModifiedBy(authenticatedUser);

        // Validate
        if (!isUniqueWikinameInDirectory() ||
            !isUniqueWikinameInArea()) return null;

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

    // Validation rules for persist(), update(), and remove();

    protected boolean isUniqueWikinameInDirectory() {
        if (parentDirectory == null) return true; // Editing wiki root
        Node foundNode = nodeDAO.findNodeInDirectory(parentDirectory, getInstance().getWikiname());
        if (foundNode != null && foundNode != getInstance()) {
            getFacesMessages().addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateName",
                "This name is already used, please change it."
            );
            return false;
        }
        return true;
    }

    protected boolean isUniqueWikinameInArea() {
        if (parentDirectory == null) return true; // Editing wiki root
        Node foundNode = nodeDAO.findNodeInArea(parentDirectory.getAreaNumber(), getInstance().getWikiname());
        if (foundNode != null && foundNode != getInstance()) {
            getFacesMessages().addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateNameInArea",
                "This name is already used in this area, please change it."
            );
            return false;
        }
        return true;
    }

}
