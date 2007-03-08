package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.Events;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;

import java.util.List;
import java.util.Collections;

@Name("directoryHome")
@Scope(ScopeType.CONVERSATION)
public class DirectoryHome extends NodeHome<Directory> {

    @Override
    @Transactional
    public void create() {
        super.create();

        // Fill the datamodel for outjection
        refreshChildNodes();
    }


    @Override
    @Transactional
    public String persist() {

        if (parentDirectory.getParent() != null) {
            // This is a subdirectory in an area
            getInstance().setAreaNumber(parentDirectory.getAreaNumber());
            return super.persist();
        } else {
            // This is a logical area

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


    @Override
    public String remove() {
        if (getInstance().getParent() == null) return null; // Can not delete wiki root
        return super.remove();
    }

    @DataModel
    List<Node> childNodes;

    @DataModelSelection
    Node selectedChildNode;

    public void moveNodeUpInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position-1, position+1), 1);
        refreshChildNodes();
    }

    public void moveNodeDownInList() {
        int position = getInstance().getChildren().indexOf(selectedChildNode);
        Collections.rotate(getInstance().getChildren().subList(position, position+2), 1);
        refreshChildNodes();
    }

    public void selectDefaultDocument() {
        getInstance().setDefaultDocument((Document)selectedChildNode);
        refreshChildNodes();
    }

    private void refreshChildNodes() {
        childNodes = getInstance().getChildren();
    }

    public void previewMenuItems() {
        // Refresh UI
        Events.instance().raiseEvent("Nodes.menuStructureModified");
    }

}
