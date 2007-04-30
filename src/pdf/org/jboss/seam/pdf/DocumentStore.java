package org.jboss.seam.pdf;

import java.io.Serializable;
import java.util.*;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

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
       String url = getFacesContext().getApplication().getViewHandler().getActionURL(getFacesContext(), "/seam-doc." + getDefaultSuffix(getFacesContext()));
       String baseUrl = getFacesContext().getExternalContext().encodeActionURL(url);
        
       if (useExtensions) {
           baseUrl = baseName + "." + docType.getExtension();
       } 
        
       return baseUrl + "?docId=" + contentId;
    }
    
    private FacesContext getFacesContext() 
    {
       return FacesContext.getCurrentInstance().getCurrentInstance();
    }
    
    private static String getDefaultSuffix(FacesContext context) throws FacesException {
        ExternalContext externalContext = context.getExternalContext();
        String viewSuffix = externalContext.getInitParameter(ViewHandler.DEFAULT_SUFFIX_PARAM_NAME);
        return (viewSuffix != null) ? viewSuffix : ViewHandler.DEFAULT_SUFFIX;
   }
    
}
   
