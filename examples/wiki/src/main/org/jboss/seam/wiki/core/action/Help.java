package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.framework.EntityNotFoundException;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.action.prefs.WikiPreferences;
import org.jboss.seam.wiki.core.nestedset.NestedSetNodeWrapper;
import org.jboss.seam.wiki.util.WikiUtil;

@Name("help")
@Scope(ScopeType.PAGE)
public class Help {

    @In
    NodeDAO nodeDAO;

    @In
    WikiPreferences wikiPreferences;

    NestedSetNodeWrapper<Node> root;

    public NestedSetNodeWrapper<Node> getRoot() {
        // If this is the first time or if the preferences changed... (re)load the help document tree
        if (root == null || !root.getWrappedNode().getName().equals(wikiPreferences.getHelpArea()) ) {
            Directory helpAreaRoot = nodeDAO.findArea(WikiUtil.convertToWikiName(wikiPreferences.getHelpArea()));
            if (helpAreaRoot != null) {
                root = nodeDAO.findMenuItems(helpAreaRoot, 99l, 1l, false);
            } else {
                throw new EntityNotFoundException("Help Area: '" + wikiPreferences.getHelpArea() + "'", Directory.class);
            }
        }
        return root;
    }

    Document selectedDocument;
    Directory selectedDirectory;
    public Document getSelectedDocument() { return selectedDocument; }
    public Directory getSelectedDirectory() { return selectedDirectory; }

    NestedSetNodeWrapper<Node> selectedNode;
    public NestedSetNodeWrapper<Node> getSelectedNode() { return selectedNode; }
    public void setSelectedNode(NestedSetNodeWrapper<Node> selectedNode) {
        this.selectedNode = selectedNode;

        selectedDirectory = null;
        selectedDocument = null;
        if (selectedNode != null) {
            if (WikiUtil.isDirectory( selectedNode.getWrappedNode() )) {
                selectedDirectory = (Directory)selectedNode.getWrappedNode();
            } else if (WikiUtil.isDocument( selectedNode.getWrappedNode() ) ) {
                selectedDocument = (Document)selectedNode.getWrappedNode();
                selectedDirectory = selectedNode.getWrappedNode().getParent();
            }
        } else {
            selectedDirectory = (Directory)root.getWrappedNode();
        }
    }

    public void selectDocumentByName(String documentName) {
        Node foundNode = nodeDAO.findDocumentInArea(root.getWrappedNode().getAreaNumber(), WikiUtil.convertToWikiName(documentName));
        if (foundNode == null)
            throw new EntityNotFoundException("Help document: "+documentName, Document.class);

        setSelectedNode(new NestedSetNodeWrapper(foundNode));
    }

}
