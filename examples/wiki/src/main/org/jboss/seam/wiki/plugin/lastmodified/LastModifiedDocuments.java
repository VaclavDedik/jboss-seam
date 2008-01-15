/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.lastmodified;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.io.Serializable;
import java.util.List;

@Name("lastModifiedDocuments")
@Scope(ScopeType.PAGE)
public class LastModifiedDocuments implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In("#{preferences.get('LastModifiedDocuments', currentMacro)}")
    LastModifiedDocumentsPreferences prefs;

    private List<WikiDocument> listOfDocuments;

    public List<WikiDocument> getListOfDocuments() {
        if (listOfDocuments == null) loadDocuments();
        return listOfDocuments;
    }

    @Observer(value = "Macro.render.lastModifiedDocuments", create = false)
    public void loadDocuments() {
        listOfDocuments =
            wikiNodeDAO.findWikiDocuments(
                Long.valueOf(prefs.getNumberOfItems()).intValue(),
                WikiNode.SortableProperty.lastModifiedOn,
                false
            );
    }

}
