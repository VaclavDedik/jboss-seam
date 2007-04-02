package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.dao.NodeDAO;

import java.util.List;
import java.io.Serializable;

@Name("lastModifiedDocumentsPlugin")
@Scope(ScopeType.PAGE)
public class LastModifiedDocuments implements Serializable {

    @In
    NodeDAO nodeDAO;

    @In("#{lastModifiedDocumentsPreferences.properties['numberOfItems']}")
    private Long numberOfItems;

    private List<Document>lastModifiedDocuments;

    public List<Document> getLastModifiedDocuments() {
        if (lastModifiedDocuments == null) loadDocuments();
        return lastModifiedDocuments;
    }

    @Observer("Preferences.lastModifiedDocumentsPreferences")
    public void loadDocuments() {
        lastModifiedDocuments = nodeDAO.findDocumentsOrderByLastModified(Long.valueOf(numberOfItems).intValue());
    }

}
