package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.dao.NodeDAO;

import java.util.List;
import java.io.Serializable;

@Name("lastModified")
@Scope(ScopeType.PAGE)
public class LastModified implements Serializable {

    @In
    NodeDAO nodeDAO;

    @Factory(value = "lastModifiedDocuments", scope = ScopeType.PAGE)
    public List<Document> getLastModifiedDocuments() {
        return nodeDAO.findDocumentsOrderByLastModified(5);
    }

}
