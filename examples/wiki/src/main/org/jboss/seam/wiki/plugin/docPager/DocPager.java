/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.docPager;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;

import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("docPager")
@Scope(ScopeType.PAGE)
public class DocPager implements Serializable {

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiDocument currentDocument;

    @In("#{preferences.get('DocPager', currentMacro)}")
    DocPagerPreferences prefs;

    private WikiDocument previous;
    private WikiDocument next;

    public WikiDocument getPrevious() {
        return previous;
    }

    public WikiDocument getNext() {
        return next;
    }

    @Create
    @Observer(value = "Macro.render.docPager", create = false)
    public void loadSibling() {

        // By default, previous/next documents are searched by creation date
        WikiNode.SortableProperty byProperty = WikiNode.SortableProperty.createdOn;
        if (prefs.getByProperty() != null) {
            try {
                byProperty = WikiNode.SortableProperty.valueOf(prefs.getByProperty());
            } catch (IllegalArgumentException ex) {}
        }

        previous = wikiNodeDAO.findSiblingWikiDocumentInDirectory(currentDocument, byProperty, true);
        next = wikiNodeDAO.findSiblingWikiDocumentInDirectory(currentDocument, byProperty, false);
        if (previous == next) {
            // Can't decide if it's previous or next because the checked property has the same value for both
            previous = null;
            next = null;
        }
    }

}