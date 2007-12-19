package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;
import org.jboss.seam.wiki.util.WikiUtil;

import java.io.Serializable;

@Name("help")
@Scope(ScopeType.SESSION)
public class Help implements Serializable {

    @Logger
    Log log;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiPreferences wikiPreferences;

    NestedSetNodeWrapper<WikiDirectory> root;

    public NestedSetNodeWrapper<WikiDirectory> getRoot() {
        if (root == null) refreshRoot();
        return root;
    }

    // TODO: Find event that triggers help document updates... difficult
    //@Observer(value = "Nodes.menuStructureModified", create = false)
    public void refreshRoot() {
        log.debug("Loading help documents tree");
         WikiDirectory helpAreaRoot = wikiNodeDAO.findArea(WikiUtil.convertToWikiName(wikiPreferences.getHelpArea()));
        if (helpAreaRoot != null) {
            root = wikiNodeDAO.findWikiDirectoryTree(helpAreaRoot, 99l, 1l, false);
        } else {
            throw new EntityNotFoundException("Help Area: '" + wikiPreferences.getHelpArea() + "'", WikiDirectory.class);
        }
    }

    // Needed for the tree dropdown
    NestedSetNodeWrapper<WikiDirectory> selectedNode;
    public NestedSetNodeWrapper<WikiDirectory> getSelectedNode() {
        return selectedNode;
    }
    public void setSelectedNode(NestedSetNodeWrapper<WikiDirectory> selectedNode) {
        this.selectedNode = selectedNode;
        setSelectedDirectory(selectedNode.getWrappedNode());
        setSelectedDocument(null);
    }
    WikiDocument selectedDocument;
    WikiDirectory selectedDirectory;

    public WikiDocument getSelectedDocument() {
        return selectedDocument;
    }

    public void setSelectedDocument(WikiDocument selectedDocument) {
        this.selectedDocument = selectedDocument;
    }

    public WikiDirectory getSelectedDirectory() {
        return selectedDirectory;
    }

    public void setSelectedDirectory(WikiDirectory selectedDirectory) {
        this.selectedDirectory = selectedDirectory;
    }

    public void selectDocumentByName(String documentName) {
        log.debug("Searching for help document with wiki name in area: " + getRoot().getWrappedNode().getAreaNumber() + ", " + WikiUtil.convertToWikiName(documentName));
        WikiDocument helpDoc =
                wikiNodeDAO.findWikiDocumentInArea(
                        getRoot().getWrappedNode().getAreaNumber(),
                        WikiUtil.convertToWikiName(documentName)
                );
        if (helpDoc == null)
            throw new EntityNotFoundException("Help document: "+documentName, WikiDocument.class);

        log.debug("Found help document: " + helpDoc);
        // TODO: Avoid cast
        setSelectedNode(new NestedSetNodeWrapper<WikiDirectory>( (WikiDirectory)helpDoc.getParent()) );
        setSelectedDocument(helpDoc);
        // TODO: Avoid cast
        setSelectedDirectory( (WikiDirectory)helpDoc.getParent() );
    }

}
