package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.util.Diff;

import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;

@Name("nodeHistory")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class NodeHistory implements Serializable {

    @In
    NodeDAO nodeDAO;

    @In
    private FacesMessages facesMessages;

    @DataModel
    private List<Node> historicalNodeList;

    @DataModelSelection
    @Out(required = false, scope = ScopeType.CONVERSATION)
    private Node selectedHistoricalNode;

    @In @Out(scope = ScopeType.CONVERSATION)
    private Node currentNode;

    @Out(scope = ScopeType.CONVERSATION)
    private Directory currentDirectory;

    private String diffResult;

    @Factory("historicalNodeList")
    public void initializeHistoricalNodeList() {
        if (historicalNodeList == null)
            historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
    }

    @Create
    public void create() {
        historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
        currentDirectory = (Directory)currentNode.getParent();

        if (historicalNodeList.size() == 0) {
            facesMessages.addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "noHistory",
                "No stored history for this document.");
            NodeBrowser browser = (NodeBrowser) Component.getInstance("browser");
            browser.exitConversation(false);
        }
    }

    public void diff() {

        String revision = ((Document)currentNode).getContent();
        String original = ((Document)selectedHistoricalNode).getContent();

        Diff diff = new Diff() {
            protected String getDeletionStartMarker() {
                return "XXXXXXX";
            }

            protected String getDeletionEndMarker() {
                return "XXXXXXX";
            }

            protected String getAdditionStartMarker() {
                return "AAAAAAA";
            }

            protected String getAdditionEndMarker() {
                return "AAAAAAA";
            }
        };

        String[] x = original.split("\r\n");
        String[] y = revision.split("\r\n");

        String[] result = diff.renderDiff(x, y, "\r\n", "\r", "\n");
        
        diffResult = Diff.renderWithDelimiter(result, "\r\n");

        System.out.println("############### RESULT OF THE DIFF: ######################");

        System.out.println(diffResult);

        System.out.println("#####################################");
    }

    public String rollback() {
        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "rollingBackDocument",
            "Rolling back to revision " + selectedHistoricalNode.getRevision());
        return "rollback";
    }

    public String getDiffResult() {
        return diffResult;
    }
}
