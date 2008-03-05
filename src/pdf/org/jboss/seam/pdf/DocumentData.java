package org.jboss.seam.pdf;

import java.io.Serializable;

public class DocumentData 
    implements Serializable
{
    byte[] data;
    DocumentType documentType;
    String baseName;

    String disposition = "inline";
    String fileName;
    
    public DocumentData(String baseName, DocumentType documentType, byte[] data) {
        super();
        this.data = data;
        this.documentType = documentType;
        this.baseName = baseName;
    }


    public byte[] getData() {
        return data;
    }
    public DocumentType getDocumentType() {
        return documentType;
    }
    public String getBaseName() {
        return baseName;
    }

    
    public void setFilename(String fileName) {
        this.fileName = fileName;
    }
    
    public String getFileName() {
        if (fileName == null) {
            return getBaseName() + "." + getDocumentType().getExtension();
        } else {
           return fileName;
        }
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getDisposition() {
        return disposition;
    }

    static public class DocumentType 
        implements Serializable
    {
        private String mimeType;
        private String extension;

        public DocumentType(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }

        public String getMimeType() {
            return mimeType;
        }

        public String getExtension(){
            return extension;
        }

    }    
}