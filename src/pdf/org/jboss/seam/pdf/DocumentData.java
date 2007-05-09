package org.jboss.seam.pdf;

public class DocumentData {
    byte[] data;
    DocType docType;
    String baseName;
    
    public DocumentData(String baseName, DocType docType, byte[] data) {
        super();
        this.data = data;
        this.docType = docType;
        this.baseName = baseName;
    }
    public byte[] getData() {
        return data;
    }
    public DocType getDocType() {
        return docType;
    }
    public String getBaseName() {
        return baseName;
    }
        
    public String getFileName() {
        return getBaseName() + "." + getDocType().getExtension();
    }
    
    public enum DocType { 
        PDF("pdf", "application/pdf"), 
        JPEG("jpg", "image/jpeg"), 
        RTF("rtf", "text/rtf"),
        HTML("html", "text/html");
        
        private String mimeType;
        private String extension;

        DocType(String extension, String mimeType) {
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