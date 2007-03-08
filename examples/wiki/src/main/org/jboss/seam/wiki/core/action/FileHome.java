package org.jboss.seam.wiki.core.action;

import javax.swing.*;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.ui.FileMetaMap;
import org.jboss.seam.wiki.core.model.File;
import org.jboss.seam.wiki.core.model.ImageMetaInfo;

import java.util.Map;

@Name("fileHome")
@Scope(ScopeType.CONVERSATION)
public class FileHome extends NodeHome<File> {

    @In
    Map<String, FileMetaMap.FileMetaInfo> fileMetaMap;

    private String filename;
    private String contentType;
    // TODO: This should really use an InputStream and directly stream into the BLOB without consuming server memory
    private byte[] filedata;
    private int imagePreviewSize = 240;


    @Override
    public String persist() {

        // Sync file instance with form data
        syncFile();

        return super.persist();
    }

    @Override
    public String update() {

        // Sync file instance with form data
        syncFile();

        return super.update();
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
}
