package org.jboss.seam.pdf;

import java.io.Serializable;
import java.util.*;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;

@Name("documentStore")
@Scope(ScopeType.CONVERSATION)
@Install(precedence=Install.BUILT_IN)
public class DocumentStore 
    implements Serializable
{    
    private static final long serialVersionUID = -357154201942127711L;

    Map<String,DocumentData> dataStore = new HashMap<String,DocumentData>();   

    long nextId = 1;
    boolean useExtensions = false;
    
    public void setUseExtensions(boolean useExtensions) {
        this.useExtensions = useExtensions;
    }
    
    public String newId() {
        return String.valueOf(nextId++);
    }

    public void saveData(String id, String baseName, DocType type, byte[] data) {
        dataStore.put(id, new DocumentData(baseName, type, data));
    }

    public byte[] dataForId(String id) {
        return dataStore.get(id).getData();
    }
    
    public String typeForId(String id) {
        return dataStore.get(id).getDocType().getMimeType();
    }
   
    public String fileNameForId(String id) {
        return dataStore.get(id).getBaseName() + "." + dataStore.get(id).getDocType().getExtension();
    }

    public static DocumentStore instance()
    {
        return (DocumentStore) Component.getInstance(DocumentStore.class, true);
    }
    
    static class DocumentData {
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
    }

    public String preferredUrlForContent(String baseName, DocType docType, String id) {
        String baseUrl = "seam-doc.seam";
        
        if (useExtensions) {
            baseUrl = baseName + "." + docType.getExtension();
        } 
        
        return baseUrl + "?docId=" + id;
    }
    
    
    public enum DocType { 
        PDF("pdf", "application/pdf"), 
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
