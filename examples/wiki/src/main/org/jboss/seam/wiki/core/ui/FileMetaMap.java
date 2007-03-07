package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.AutoCreate;

import java.util.HashMap;
import java.util.Map;

@Name("fileMetaMap")
@AutoCreate
public class FileMetaMap {

    public String contentType;

    private Map<String, FileMetaInfo> metamap = new HashMap<String, FileMetaInfo>() {
        {
            put("image/jpg",                    new FileMetaInfo("icon.fileimg.gif", true));
            put("image/jpeg",                   new FileMetaInfo("icon.fileimg.gif", true));
            put("image/gif",                    new FileMetaInfo("icon.fileimg.gif", true));
            put("image/png",                    new FileMetaInfo("icon.fileimg.gif", true));
            put("text/plain",                   new FileMetaInfo("icon.filetxt.gif", false));
            put("application/pdf",              new FileMetaInfo("icon.filepdf.gif", false));
            put("application/octet-stream",     new FileMetaInfo("icon.filegeneric.gif", false));
            put("generic",                      new FileMetaInfo("icon.filegeneric.gif", false));
        }
    };

    @Unwrap
    public Map<String, FileMetaInfo> getFielMetaMap() {
        return metamap;
    }

    public class FileMetaInfo {
        
        public String displayIcon;
        public boolean image;

        public FileMetaInfo(String displayIcon, boolean image) {
            this.displayIcon = displayIcon;
            this.image = image;
        }

        public String getDisplayIcon() { return displayIcon; }
        public boolean isImage() { return image; }
    }
}
