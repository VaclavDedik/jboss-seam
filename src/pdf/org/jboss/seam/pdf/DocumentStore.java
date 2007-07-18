package org.jboss.seam.pdf;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.pdf.DocumentData.DocType;
import org.jboss.seam.ui.util.Faces;

@Name("org.jboss.seam.pdf.documentStore")
@Scope(ScopeType.CONVERSATION)
@Install(precedence=Install.BUILT_IN)
public class DocumentStore implements Serializable
{    
    private static final long serialVersionUID = -357154201942127711L;

    Map<String,DocumentData> dataStore = new HashMap<String,DocumentData>();   

    long nextId = 1;
    boolean useExtensions = false;
    String errorPage = null;
    
    public void setUseExtensions(boolean useExtensions) 
    {
        this.useExtensions = useExtensions;
    }
    
    public void setErrorPage(String errorPage) 
    {
        this.errorPage = errorPage;
    }
    
    public String getErrorPage() 
    {
        return errorPage;
    }
    
    public String newId() 
    {
        return String.valueOf(nextId++);
    }

    public void saveData(String id, DocumentData documentData) 
    {
        dataStore.put(id, documentData);
    }

    public boolean idIsValid(String id) 
    {
        return dataStore.get(id) != null;
    }
    
    public DocumentData getDocumentData(String id) 
    {
        return dataStore.get(id);
    }
    
    public static DocumentStore instance()
    {
        return (DocumentStore) Component.getInstance(DocumentStore.class);
    }
  

    public String preferredUrlForContent(String baseName, DocType docType, String contentId) 
    {
       FacesContext context = FacesContext.getCurrentInstance();
       String url = context.getApplication().getViewHandler().getActionURL(context, "/seam-doc" + Faces.getDefaultSuffix(context));
       String baseUrl = context.getExternalContext().encodeActionURL(url);
      
       if (useExtensions) 
       {
           baseUrl = baseName + "." + docType.getExtension();
       } 
        
       return baseUrl + "?docId=" + contentId;
    }
    
    
    
    
}
   
