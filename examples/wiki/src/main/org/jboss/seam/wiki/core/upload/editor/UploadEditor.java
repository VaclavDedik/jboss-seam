package org.jboss.seam.wiki.core.upload.editor;

import org.jboss.seam.wiki.core.model.WikiUpload;

public abstract class UploadEditor<WU extends WikiUpload> {

    private WU instance;

    public void init(WU instance) {
        this.instance = instance;
    }

    public abstract String getIncludeName();

    public WU getInstance() {
        return instance;
    }

    /**
     * Called before the owning home action does its preparation;
     * @return boolean continue processing
     */
    public boolean preparePersist() { return true; }

    /**
     * Called after superclass did its preparation right before the actual persist()
     * @return boolean continue processing
     */
    public boolean beforePersist() { return true; }

    /**
     * Called before the owning home action does its preparation;
     * @return boolean continue processing
     */
    public boolean prepareUpdate() { return true; }

    /**
     * Called after superclass did its preparation right before the actual update()
     * @return boolean continue processing
     */
    public boolean beforeUpdate() { return true; }

    /**
     * Called before the owning home action does its preparation;
     * @return boolean continue processing
     */
    public boolean prepareRemove() { return true; }

    /**
     * Called after superclass did its preparation right before the actual remove()
     * @return boolean continue processing
     */
    public boolean beforeRemove() { return true; }

}
