package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.links.WikiLinkResolver;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.dao.UserDAO;
import org.jboss.seam.wiki.core.model.*;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;

import java.util.List;

@Name("documentHome")
public class DocumentHome extends EntityHome<Document> {

    @RequestParameter
    private Long docId;

    @RequestParameter
    private Long parentDirId;

    // Pages need this for rendering
    @Out(required = true, scope = ScopeType.CONVERSATION, value = "currentDirectory")
    Directory parentDirectory;

    @Out(required = true, scope = ScopeType.CONVERSATION, value = "currentDocument")
    Document currentDocument;

    @In
    private FacesMessages facesMessages;

    @In
    private NodeBrowser browser;

    @In
    private WikiLinkResolver wikiLinkResolver;

    @In
    private NodeDAO nodeDAO;

    @In
    private UserDAO userDAO;

    @In
    private User authenticatedUser;

    @In(required = false)
    Node selectedHistoricalNode;

    private Document historicalCopy;
    private String formContent;
    private boolean enabledPreview = false;
    private boolean minorRevision = true;
    private List<org.jboss.seam.wiki.core.model.Role> roles;
    private org.jboss.seam.wiki.core.model.Role writableByRole;
    private org.jboss.seam.wiki.core.model.Role readableByRole;

    @Override
    public Object getId() {

        if (docId == null) {
            return super.getId();
        } else {
            return docId;
        }
    }

    @Override
    @Transactional
    public void create() {
        super.create();

        // Settings
        GlobalPreferences globalPrefs = (GlobalPreferences) Component.getInstance("globalPrefs");
        minorRevision = !globalPrefs.isDefaultNewRevisionForEditedDocument();

        // Load the parent directory
        parentDirectory = nodeDAO.findDirectory(parentDirId);

        // Outject current document
        currentDocument = getInstance();

        // Load the availale roles and set permission defaults
        roles = userDAO.findRoles();
        writableByRole = userDAO.findRole(getInstance().getWriteAccessLevel());
        readableByRole = userDAO.findRole(getInstance().getReadAccessLevel());

        // Rollback to historical revision?
        if (selectedHistoricalNode != null) getInstance().rollback(selectedHistoricalNode);

        // Make a copy
        historicalCopy = new Document(getInstance());
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
            if (endBeforeRedirect)
                browser.redirectToLastBrowsedPage();
            else
                browser.redirectToLastBrowsedPageWithConversation();
        }
    }

    @Override
    public String persist() {

        // Validate
        if (!isUniqueWikinameInDirectory() ||
            !isUniqueWikinameInArea()) return null;

        // Link the document with a directory
        parentDirectory.addChild(getInstance());

        // Set created by user
        getInstance().setCreatedBy(authenticatedUser);

        // Set its area number
        getInstance().setAreaNumber(parentDirectory.getAreaNumber());

        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );

        // Permissions
        getInstance().setWriteAccessLevel(writableByRole != null ? writableByRole.getAccessLevel() : 1000);
        getInstance().setReadAccessLevel(readableByRole != null ? readableByRole.getAccessLevel() : 1000);

        return super.persist();
    }


    @Override
    public String update() {

        // Validate
        if (!isUniqueWikinameInDirectory() ||
            !isUniqueWikinameInArea()) return null;

        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );

        // Set last modified by user
        getInstance().setLastModifiedBy(authenticatedUser);

        Events.instance().raiseEvent("Nodes.menuStructureModified");

        // Write history log and prepare a new copy for further modification
        if (!isMinorRevision()) {
            nodeDAO.persistHistoricalNode(historicalCopy);
            getInstance().incrementRevision();
            historicalCopy = new Document(getInstance());
        }

        // Permissions
        getInstance().setWriteAccessLevel(writableByRole != null ? writableByRole.getAccessLevel() : 1000);
        getInstance().setReadAccessLevel(readableByRole != null ? readableByRole.getAccessLevel() : 1000);

        return super.update();
    }

    @Override
    public String remove() {

        // Unlink the document from its directory
        getInstance().getParent().removeChild(getInstance());

        Events.instance().raiseEvent("Nodes.menuStructureModified");

        // Delete all history nodes
        nodeDAO.removeHistoricalNodes(getInstance());

        return super.remove();
    }

    public String getFormContent() {
        // Load the document content and resolve links
        if (formContent == null)
            formContent = wikiLinkResolver.convertFromWikiLinks(parentDirectory, getInstance().getContent());
        return formContent;
    }

    public void setFormContent(String formContent) {
        this.formContent = formContent;
    }

    public boolean isEnabledPreview() {
        return enabledPreview;
    }

    public void setEnabledPreview(boolean enabledPreview) {
        this.enabledPreview = enabledPreview;
        // Convert and set form content onto entity instance
        getInstance().setContent(
            wikiLinkResolver.convertToWikiLinks(parentDirectory, getFormContent())
        );
    }

    public boolean isMinorRevision() {
        return minorRevision;
    }

    public void setMinorRevision(boolean minorRevision) {
        this.minorRevision = minorRevision;
    }

    public List<org.jboss.seam.wiki.core.model.Role> getRoles() {
        return roles;
    }

    public org.jboss.seam.wiki.core.model.Role getWritableByRole() {
        return writableByRole;
    }

    public void setWritableByRole(org.jboss.seam.wiki.core.model.Role writableByRole) {
        this.writableByRole = writableByRole;
    }

    public org.jboss.seam.wiki.core.model.Role getReadableByRole() {
        return readableByRole;
    }

    public void setReadableByRole(org.jboss.seam.wiki.core.model.Role readableByRole) {
        this.readableByRole = readableByRole;
    }

    // Validation rules for persist(), update(), and remove();

    private boolean isUniqueWikinameInDirectory() {
        Node foundNode = nodeDAO.findNodeInDirectory(parentDirectory, getInstance().getWikiname());
        if (foundNode != null && foundNode != getInstance()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateName",
                "This name is already used, please change it."
            );
            return false;
        }
        return true;
    }

    private boolean isUniqueWikinameInArea() {
        Node foundNode = nodeDAO.findNodeInArea(parentDirectory.getAreaNumber(), getInstance().getWikiname());
        if (foundNode != null && foundNode != getInstance()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
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
