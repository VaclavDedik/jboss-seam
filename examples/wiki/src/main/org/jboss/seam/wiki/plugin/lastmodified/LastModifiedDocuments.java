package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Document;

import java.io.Serializable;
import java.util.List;

@Name("lastModifiedDocumentsPlugin")
@Scope(ScopeType.PAGE)
public class LastModifiedDocuments implements Serializable {

    @In
    NodeDAO nodeDAO;

    @In("#{lastModifiedDocumentsPreferences.numberOfItems}")
    private Long numberOfItems;

    private List<Document>lastModifiedDocuments;

    public List<Document> getLastModifiedDocuments() {
        if (lastModifiedDocuments == null) loadDocuments();
        return lastModifiedDocuments;
    }

    @Observer("PreferenceComponent.refresh.lastModifiedDocumentsPreferences")
    public void loadDocuments() {
        lastModifiedDocuments = nodeDAO.findDocumentsOrderByLastModified(Long.valueOf(numberOfItems).intValue());
    }

}
