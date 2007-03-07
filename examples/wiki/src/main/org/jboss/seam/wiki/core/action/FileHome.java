package org.jboss.seam.wiki.core.action;

import static javax.faces.application.FacesMessage.SEVERITY_ERROR;
import javax.swing.*;

import org.jboss.seam.annotations.*;
import org.jboss.seam.framework.EntityHome;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.ui.FileMetaMap;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.model.ImageMetaInfo;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Conversation;

import java.util.Map;

@Name("fileHome")
public class FileHome extends EntityHome<File> {

    @RequestParameter
    private Long fileId;

    @RequestParameter
    private Long parentDirId;

    // Pages need this for rendering
    @Out(required = true, scope = ScopeType.CONVERSATION, value = "currentDirectory")
    Directory parentDirectory;

    @In
    private FacesMessages facesMessages;

    @In
    private NodeBrowser browser;

    @In
    private NodeDAO nodeDAO;

    @In
    Map<String, FileMetaMap.FileMetaInfo> fileMetaMap;

    private String filename;
    private String contentType;
    // TODO: This should really use an InputStream and directly stream into the BLOB without consuming server memory
    private byte[] filedata;

    private int imagePreviewSize = 240;

    @Override
    public Object getId() {

        if (fileId == null) {
            return super.getId();
        } else {
            return fileId;
        }
    }

    @Override
    @Transactional
    public void create() {
        super.create();

        // Load the parent directory
        getEntityManager().joinTransaction();
        parentDirectory = getEntityManager().find(Directory.class, parentDirId);
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


        // Sync file instance with form data
        syncFile();

        // Link the document with a directory
        parentDirectory.addChild(getInstance());

        // Set its area number
        getInstance().setAreaNumber(parentDirectory.getAreaNumber());

        return super.persist();
    }


    @Override
    public String update() {

        // Validate
        if (!isUniqueWikinameInDirectory() ||
            !isUniqueWikinameInArea()) return null;

        // Sync file instance with form data
        syncFile();

        return super.update();
    }

    @Override
    public String remove() {

        // Unlink the document from its directory
        getInstance().getParent().removeChild(getInstance());
/*
        Events.instance().raiseEvent("Nodes.menuStructureModified");
*/
        return super.remove();
    }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public byte[] getFiledata() { return filedata; }
    public void setFiledata(byte[] filedata) { this.filedata = filedata; }

    private void syncFile() {
        if (filedata != null && filedata.length >0) {
            getInstance().setFilename(filename);
            getInstance().setFilesize(filedata.length); // Don't trust the browsers headers!
            getInstance().setData(filedata);
            getInstance().setContentType(contentType);

            // Handle image/picture meta info
            if (fileMetaMap.get(getInstance().getContentType()).image) {

                ImageMetaInfo imageMetaInfo =
                        getInstance().getImageMetaInfo() != null
                                ? getInstance().getImageMetaInfo()
                                : new ImageMetaInfo();
                getInstance().setImageMetaInfo(imageMetaInfo);

                ImageIcon icon = new ImageIcon(getInstance().getData());
                int imageSizeX = icon.getImage().getWidth(null);
                int imageSizeY = icon.getImage().getHeight(null);
                getInstance().getImageMetaInfo().setSizeX(imageSizeX);
                getInstance().getImageMetaInfo().setSizeY(imageSizeY);
            }
        }
    }

    public int getImagePreviewSize() {
        return imagePreviewSize;
    }

    public void zoomPreviewIn() {
        if (imagePreviewSize < 1600) imagePreviewSize = imagePreviewSize + 240;
    }

    public void zoomPreviewOut() {
        if (imagePreviewSize > 240) imagePreviewSize = imagePreviewSize - 240;
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
