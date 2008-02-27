/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.dirToc;

import org.hibernate.Query;
import org.hibernate.Session;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.richfaces.component.UITree;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.*;

/**
 * @author Christian Bauer
 */
@Name("dirTocQuery")
@Scope(ScopeType.CONVERSATION)
public class DirTocQuery implements Serializable {

    @In
    EntityManager restrictedEntityManager;

    @In
    Integer currentAccessLevel;

    @In
    WikiDirectory currentDirectory;

    @In("#{preferences.get('DirToc', currentMacro)}")
    DirTocPreferences prefs;

    @In
    WikiNodeDAO wikiNodeDAO;

    NestedSetNodeWrapper<WikiDirectory> tocRoot;

    public NestedSetNodeWrapper<WikiDirectory> getTocRoot() {
        if (tocRoot == null) loadTocRoot();
        return tocRoot;
    }

    @Observer(value = "Macro.render.dirToc", create = false)
    public void loadTocRoot() {

        if (prefs.getRootDocumentLink() != null) {
            Long id = ((WikiLinkResolver)Component.getInstance("wikiLinkResolver"))
                        .resolveWikiDirectoryId(currentDirectory.getAreaNumber(), prefs.getRootDocumentLink());
            WikiDirectory foundDir = wikiNodeDAO.findWikiDirectory(id);
            if (foundDir != null)
                currentDirectory = foundDir;
        }

        // Query the directory tree
        tocRoot = wikiNodeDAO.findWikiDirectoryTree(currentDirectory);

        Set<Long> directoryIds = new HashSet<Long>(tocRoot.getFlatTree().keySet());
        if (prefs.getShowRootDocuments() != null && prefs.getShowRootDocuments()) {
            directoryIds.add(tocRoot.getWrappedNode().getId());
        }
        if (directoryIds.size() == 0) return; // Early exit

        // Now query the documents for the directories in the tree
        StringBuilder queryString = new StringBuilder();
        queryString.append("select d from WikiDocument d ");
        // TODO: Rewrite this query to use a subselect nested set query, this has limits
        queryString.append("where d.parent.id in (:directories) ");
        if (prefs.getWithHeaderMacro() != null)
            queryString.append("and d.headerMacrosString like :headerMacro").append(" ");
        queryString.append("order by d.createdOn asc");

        Query query = getSession().createQuery(queryString.toString());
        query.setParameterList("directories", directoryIds);
        if (prefs.getWithHeaderMacro() != null)
            query.setParameter("headerMacro", "%"+prefs.getWithHeaderMacro()+"%");
        query.setComment("retrieving documents for toc directory tree");

        List<WikiDocument> flatDocs = query.list();

        // Now attach the documents as payloads to the directories in the tree
        for (WikiDocument flatDoc : flatDocs) {

            Long directoryId = flatDoc.getParent().getId();
            NestedSetNodeWrapper<WikiDirectory> dirWrapper;

            if (prefs.getShowRootDocuments() != null && prefs.getShowRootDocuments()
                && directoryId.equals(tocRoot.getWrappedNode().getId())) {
                dirWrapper = tocRoot;
            } else {
                dirWrapper = tocRoot.getFlatTree().get(directoryId);
            }

            if (dirWrapper.getPayload() == null)
                dirWrapper.setPayload(new ArrayList<WikiDocument>());

            ((Collection)dirWrapper.getPayload()).add(flatDoc);
        }

    }

    public boolean expandTocTree(UITree tree) {
        return true; // Expand all nodes by default;
    }

    private Session getSession() {
        return ((Session)((org.jboss.seam.persistence.EntityManagerProxy) restrictedEntityManager).getDelegate());
    }

}
