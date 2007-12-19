package org.jboss.seam.wiki.core.upload.editor;

import org.jboss.seam.wiki.core.model.WikiUploadImage;
import org.jboss.seam.wiki.util.WikiUtil;

public class WikiUploadImageEditor extends UploadEditor<WikiUploadImage> {

    public String getIncludeName() {
        return "wikiUploadImageEditor";
    }

    public static final int PREVIEW_SIZE_MIN  = 60;
    public static final int PREVIEW_SIZE_MAX  = 1320;
    public static final int PREVIEW_ZOOM_STEP = 120;

    private int imagePreviewSize = PREVIEW_SIZE_MIN;
    public int getImagePreviewSize() { return imagePreviewSize; }

    public void zoomActualSize() {
        imagePreviewSize = getInstance().getSizeX();
    }

    public void zoomPreviewIn() {
        if (imagePreviewSize < PREVIEW_SIZE_MAX) imagePreviewSize = imagePreviewSize + PREVIEW_ZOOM_STEP;
    }

    public void zoomPreviewOut() {
        if (imagePreviewSize > PREVIEW_SIZE_MIN && (imagePreviewSize - PREVIEW_ZOOM_STEP) > PREVIEW_SIZE_MIN)
            imagePreviewSize = imagePreviewSize - PREVIEW_ZOOM_STEP;
        else imagePreviewSize = PREVIEW_SIZE_MIN;
    }

    public boolean beforePersist() {
        generateThumbnailData();
        return super.beforePersist();
    }

    public boolean beforeUpdate() {
        generateThumbnailData();
        return super.beforeUpdate();
    }

    protected void generateThumbnailData() {
        getInstance().setThumbnailData(
            WikiUtil.resizeImage(getInstance().getData(), getInstance().getContentType(), getThumbnailWidth())
        );
    }

    public void selectThumbnail() {
        if (getInstance().getThumbnail() == 'F')
            imagePreviewSize = getInstance().getSizeX();
        else
            imagePreviewSize = getThumbnailWidth();
    }

    public int getThumbnailWidth() {
        int thumbnailWidth = 80;
        // TODO: We could make these sizes customizable
        switch (getInstance().getThumbnail()) {
            case 'M':
                thumbnailWidth = 160;
                break;
            case 'L':
                thumbnailWidth = 320;
                break;
        }
        return thumbnailWidth;
    }

}