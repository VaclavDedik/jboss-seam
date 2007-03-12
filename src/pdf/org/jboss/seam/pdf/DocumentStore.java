package org.jboss.seam.pdf;

import java.io.Serializable;
import java.util.*;

import org.jboss.seam.*;
import org.jboss.seam.annotations.*;
import org.jboss.seam.pdf.DocumentData.DocType;

@Name("org.jboss.seam.pdf.documentStore")
@Scope(ScopeType.CONVERSATION)
@Install(precedence=Install.BUILT_IN)
public class DocumentStore 
    implements Serializable
{    
    private static final long serialVersionUID = -357154201942127711L;

    Map<String,DocumentData> dataStore = new HashMap<String,DocumentData>();   

    long nextId = 1;
    boolean useExtensions = false;
    String errorPage = null;
    
    public void setUseExtensions(boolean useExtensions) {
        this.useExtensions = useExtensions;
    }
    
    public void setErrorPage(String errorPage) {
        this.errorPage = errorPage;
    }
    
    public String getErrorPage() {
        return errorPage;
    }
    
    public String newId() {
        return String.valueOf(nextId++);
    }

    public void saveData(String id, DocumentData documentData) {
        dataStore.put(id, documentData);
    }

    public boolean idIsValid(String id) {
        return dataStore.get(id) != null;
    }
    
    public DocumentData getDocumentData(String id) { 
        return dataStore.get(id);
    }
    
    public static DocumentStore instance()
    {
        return (DocumentStore) Component.getInstance(DocumentStore.class, true);
    }
  

    public String preferredUrlForContent(String baseName, DocType docType, String contentId) {
        String baseUrl = "seam-doc.seam";
        
        if (useExtensions) {
            baseUrl = baseName + "." + docType.getExtension();
        } 
        
        return baseUrl + "?docId=" + contentId;
    }
    
}
   
