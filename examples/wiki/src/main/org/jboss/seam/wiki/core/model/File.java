package org.jboss.seam.wiki.core.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("FILE")
@SecondaryTable(
    name = "NODE_FILE",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "FILE_ID")
)
@org.hibernate.annotations.Table(
    appliesTo = "NODE_FILE",
    foreignKey = @org.hibernate.annotations.ForeignKey(name = "FK_NODE_FILE_FILE_ID")
)
public class File extends Node {

    @Column(table = "NODE_FILE", name = "FILENAME", length = 255, nullable = false)
    private String filename;

    @Column(table = "NODE_FILE", name = "FILESIZE", nullable = false)
    private int filesize;

    // SchemaExport needs length.. MySQL has "tinyblob", "mediumblob" and other such nonsense types
    @Lob
    @Column(table = "NODE_FILE", name = "FILEDATA", nullable = false, length = 10000000)
    private byte[] data;

    @Column(table = "NODE_FILE", name = "CONTENT_TYPE", length = 255)
    private String contentType;

    @AttributeOverrides({
        @AttributeOverride(
            name = "sizeX",
            column = @Column(table = "NODE_FILE", name = "IMAGE_SIZE_X")
        ),
        @AttributeOverride(
            name = "sizeY",
            column = @Column(table = "NODE_FILE", name = "IMAGE_SIZE_Y")
        ),
        @AttributeOverride(
            name = "thumbnail",
            column = @Column(table = "NODE_FILE", name = "IMAGE_THUMBNAIL")
        )
    })
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
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ImageMetaInfo getImageMetaInfo() {
        return imageMetaInfo;
    }

    public void setImageMetaInfo(ImageMetaInfo imageMetaInfo) {
        this.imageMetaInfo = imageMetaInfo;
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

}
