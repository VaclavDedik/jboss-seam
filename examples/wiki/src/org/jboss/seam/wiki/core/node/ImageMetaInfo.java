package org.jboss.seam.wiki.core.node;

import javax.persistence.Embeddable;
import javax.persistence.Column;

@Embeddable
public class ImageMetaInfo {

    @Column(name = "IMAGE_SIZE_X")
    private int sizeX;

    @Column(name = "IMAGE_SIZE_Y")
    private int sizeY;

    @Column(name = "IMAGE_THUMBNAIL")
    private char thumbnail = 'A'; // Disable embedding by default, attach


    public ImageMetaInfo() {}

    public ImageMetaInfo(int sizeX, int sizeY, boolean embeddable, char thumbnail) {
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
