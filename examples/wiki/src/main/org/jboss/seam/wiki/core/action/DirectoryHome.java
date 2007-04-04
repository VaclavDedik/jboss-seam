package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<Directory> {

    /* -------------------------- Context Wiring ------------------------------ */

    @DataModel
    List<Node> childNodes;

    @DataModelSelection
    Node selectedChildNode;

    /* -------------------------- Internal State ------------------------------ */

    private List<Document> childDocuments = new ArrayList<Document>();
    public List<Document> getChildDocuments() { return childDocuments; }

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    public void create() {
        super.create();

        // Fill the datamodel for outjection
        refreshChildNodes();
    }

    /* -------------------------- Custom CUD ------------------------------ */

    @Override
    public String persist() {

        if (getParentDirectory().getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(getParentDirectory().getAreaNumber());
            return super.persist();
        } else {
            // This is a logical area in the wiki root

            // Satisfy NOT NULL constraint
            getInstance().setAreaNumber(Long.MAX_VALUE);

            // Do the persist() first, we need the identifier after this
            String outcome = super.persist();

            getInstance().setAreaNumber(getInstance().getId());

            // And flush() again...
            getEntityManager().flush();
            return outcome;
        }
    }

    protected boolean prepareRemove() {
        return getInstance().getParent() != null; // Can not delete wiki root
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void refreshChildNodes() {
        childNodes = getInstance().getChildren();
        for (Node childNode : childNodes) {
            if (childNode instanceof Document) childDocuments.add((Document)childNode);
        }
    }

    /* -------------------------- Public Features ------------------------------ */

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveNodeUpInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position-1, position+1), 1);
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void moveNodeDownInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position, position+2), 1);
        refreshChildNodes();
    }

    @Restrict("#{s:hasPermission('Node', 'editMenu', directoryHome.instance)}")
    public void previewMenuItems() {
        refreshMenuItems();
    }
}
