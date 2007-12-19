package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;

import java.io.Serializable;
import java.util.List;

@Name("lastModifiedDocumentsPlugin")
@Scope(ScopeType.PAGE)
public class LastModifiedDocuments implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In("#{lastModifiedDocumentsPreferences.numberOfItems}")
    private Long numberOfItems;

    private List<WikiDocument>lastModifiedDocuments;

    public List<WikiDocument> getLastModifiedDocuments() {
        if (lastModifiedDocuments == null) loadDocuments();
        return lastModifiedDocuments;
    }

    @Observer("PreferenceComponent.refresh.lastModifiedDocumentsPreferences")
    public void loadDocuments() {
        lastModifiedDocuments = wikiNodeDAO.findWikiDocumentsOrderByLastModified(Long.valueOf(numberOfItems).intValue());
    }

}
