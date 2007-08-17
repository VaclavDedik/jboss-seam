/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.io.Serializable;

@Embeddable
public class ImageMetaInfo implements Serializable {

    @Column(name = "IMAGE_SIZE_X")
    private int sizeX;

    @Column(name = "IMAGE_SIZE_Y")
    private int sizeY;

    @Column(name = "IMAGE_THUMBNAIL")
    private char thumbnail = 'A'; // Disable embedding by default, attach

    // SchemaExport needs length.. MySQL has "tinyblob", "mediumblob" and other such nonsense types
    @Lob
    @Column(name = "IMAGE_THUMBNAIL_DATA", length = 1000000)
    @Basic(fetch = FetchType.LAZY) // Lazy loaded through bytecode instrumentation
    private byte[] thumbnailData;

    public ImageMetaInfo() {}

    public ImageMetaInfo(int sizeX, int sizeY, char thumbnail) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.thumbnail = thumbnail;
    }

    public int getSizeX() {
        return sizeX;
    }

    public void setSizeX(int sizeX) {
        this.sizeX = sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public void setSizeY(int sizeY) {
        this.sizeY = sizeY;
    }

    public char getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(char thumbnail) {
        this.thumbnail = thumbnail;
    }

    public byte[] getThumbnailData() {
        return thumbnailData;
    }

    public void setThumbnailData(byte[] thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageMetaInfo that = (ImageMetaInfo) o;

        if (sizeX != that.sizeX) return false;
        if (sizeY != that.sizeY) return false;
        if (thumbnail != that.thumbnail) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = sizeX;
        result = 31 * result + sizeY;
        result = 31 * result + (int) thumbnail;
        return result;
    }
}
