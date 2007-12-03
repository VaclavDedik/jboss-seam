package org.jboss.seam.pdf;

public class DocumentData {
    byte[] data;
    DocumentType documentType;
    String baseName;

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

    public String getFileName() {
        return getBaseName() + "." + getDocumentType().getExtension();
    }

    static public class DocumentType {
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