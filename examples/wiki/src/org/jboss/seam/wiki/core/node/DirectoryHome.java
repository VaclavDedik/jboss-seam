package org.jboss.seam.wiki.core.node;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Conversation;

import javax.persistence.Query;
import java.util.List;
import java.util.Collections;

@Name("directoryHome")
public class DirectoryHome extends EntityHome<Directory> {

    @RequestParameter
    Long dirId;

    @RequestParameter
    Long parentDirId;

    Directory parentDirectory;

    @In(required = false)
    @Out(required = false, scope = ScopeType.CONVERSATION) // Propagate it through the conversation
    Directory currentDirectory;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private NodeBrowser browser;

    @In(create=true)
    private NodeDAO nodeDAO;

    @Override
    public Object getId() {

        if (dirId == null) {
            return super.getId();
        } else {
            return dirId;
        }
    }

    @Override
    @Transactional
    public void create() {
        super.create();

        currentDirectory = getInstance(); // Prepare for outjection

        getEntityManager().joinTransaction();
        if (parentDirId != null) {
            parentDirectory = getEntityManager().find(Directory.class, parentDirId);
        } else {
            parentDirectory = getInstance().getParent();
        }

        // Fill the datamodel for outjection
        refreshChildNodes();
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
        if (!isUniqueWikinameInDirectory(null) ||
            !isUniqueWikinameInArea()) return null;

        // Link the directory with its parent
        parentDirectory.addChild(getInstance());

        if (parentDirectory.getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());
            return super.persist();
        } else {
            // This is a logical area

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
    public String update() {

        // Validate
        if (!isUniqueWikinameInDirectory(getInstance()) ||
            !isUniqueWikinameInArea()) return null;

        Events.instance().raiseEvent("Nodes.menuStructureModified");

        return super.update();
    }

    @Override
    public String remove() {

        // Unlink the document from its parent directory
        parentDirectory.removeChild(getInstance());

        // Null the outjected value
        currentDirectory = null;

        Events.instance().raiseEvent("Nodes.menuStructureModified");

        return super.remove();
    }

    @Override
    public String getUpdatedMessage() {
        return super.getUpdatedMessage() + ": '" + getInstance().getName() + "'";
    }


    @Override
    public String getDeletedMessage() {
        return super.getDeletedMessage() + ": '" + getInstance().getName() + "'";
    }

    @Override
    public String getCreatedMessage() {
        return super.getCreatedMessage() + ": '" + getInstance().getName() + "'";
    }

    public Directory getParentDirectory() {
        return parentDirectory;
    }

    public void setParentDirectory(Directory parentDirectory) {
        this.parentDirectory = parentDirectory;
    }

    @DataModel
    List<Node> childNodes;

    @DataModelSelection
    Node selectedChildNode;

    public void moveNodeUpInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position-1, position+1), 1);
        refreshChildNodes();
    }

    public void moveNodeDownInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position, position+2), 1);
        refreshChildNodes();
    }

    public void selectDefaultDocument() {
        getInstance().setDefaultDocument((Document)selectedChildNode);
        refreshChildNodes();
    }

    private void refreshChildNodes() {
        childNodes = getInstance().getChildren();
    }

    public void previewMenuItems() {
        Events.instance().raiseEvent("Nodes.menuStructureModified");
    }

    // Validation rules for persist(), update(), and remove();

    @Transactional
    private boolean isUniqueWikinameInDirectory(Directory ignore) {
        getEntityManager().joinTransaction();

        String queryString = "select n from Node n where n.parent = :parent and n.wikiname = :wikiname";
        if (ignore != null)  queryString = queryString + " and not n = :ignore";

        Query q = getEntityManager().createQuery(queryString);
        if (ignore != null) q.setParameter("ignore", ignore);

        // Unique directory name within parent
        List existingChildren = q
                .setParameter("parent", parentDirectory)
                .setParameter("wikiname", getInstance().getWikiname())
                .getResultList();
        if (existingChildren.size() >0) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateName",
                "Directory or document with that name already exists."
            );
            return false;
        }
        return true;
    }

    /**
     * This is used to check for duplicate directory names in area. We could allow duplicate
     * directory names from a logical/automatic linking perspective, but the database constraint
     * would require a custom trigger. If we don't allow duplicate directory names in a logical
     * area, we can apply a simple multicolumn UNIQUE constraint, that is a lot easier.
     *
     * @return boolean True if the current instances WikiName already exists in the parents area
     */
    @Transactional
    private boolean isUniqueWikinameInArea() {
        if (parentDirectory == null) return true;
        getEntityManager().joinTransaction();
        // Unique directory name within area
        Directory foundDirectory =
                nodeDAO.findDirectoryInArea(parentDirectory.getAreaNumber(), getInstance().getWikiname());
        if (foundDirectory != null && foundDirectory != getInstance()) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "name",
                SEVERITY_ERROR,
                getMessageKeyPrefix() + "duplicateNameInArea",
                "Directory with that name already exists in this area."
            );
            return false;
        }
        return true;
    }

}
