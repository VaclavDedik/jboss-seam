package org.jboss.seam.wiki.core.node;

import org.hibernate.validator.Length;
import org.hibernate.validator.Pattern;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Column;
import javax.persistence.Lob;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("FILE")
public class File extends Node {

    @Column(name = "FILENAME", length = 255)
    private String filename;

    @Column(name = "FILESIZE")
    private int filesize;

    @Lob
    @Column(name = "FILEDATA")
    private byte[] data;

    @Column(name = "CONTENT_TYPE")
    private String contentType;

    private ImageMetaInfo imageMetaInfo;

    public File() { super("New File"); }

    public File(String name) {
        super(name);
    }

    // Mutable properties

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getFilesize() {
        return filesize;
    }

    public void setFilesize(int filesize) {
        this.filesize = filesize;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
        makeDirty();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        makeDirty();
    }

    public ImageMetaInfo getImageMetaInfo() {
        return imageMetaInfo;
    }

    public void setImageMetaInfo(ImageMetaInfo imageMetaInfo) {
        this.imageMetaInfo = imageMetaInfo;
        makeDirty();
    }

    public Directory getParent() {
        return (Directory)super.getParent();
    }

    public void addChild(Node child) {
        throw new UnsupportedOperationException("Files can't have children");
    }

    public void removeChild(Node child) {
        throw new UnsupportedOperationException("Files can't have children");
    }

    public String toString() {
        return getName();
    }

    public String getHumanReadableFilesize() {
        // TODO: Yeah, that could be done smarter..
        if (getFilesize() >= 1073741824) {
            return new BigDecimal(getFilesize() / 1024 / 1024 / 1024) + " GiB";
        }else if (getFilesize() >= 1048576) {
            return new BigDecimal(getFilesize() / 1024 / 1024) + "MiB";
        } else if (getFilesize() >= 1024) {
            return new BigDecimal(getFilesize() / 1024) + " KiB";
        } else {
            return new BigDecimal(getFilesize()) + " Bytes";
        }
    }

}
