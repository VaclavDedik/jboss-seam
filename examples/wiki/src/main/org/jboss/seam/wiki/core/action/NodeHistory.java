package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Conversation;
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
    private NodeBrowser browser;

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
    public void initialize() {
        historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
        currentDirectory = (Directory)currentNode.getParent();
    }

    // TODO: Typical exit method to get out of a root or nested conversation, JBSEAM-906
    public void exitConversation(Boolean endBeforeRedirect) {
        Conversation currentConversation = Conversation.instance();
        if (currentConversation.isNested()) {
            // End this nested conversation and return to last rendered view-id of parent
            currentConversation.endAndRedirect(endBeforeRedirect);
        } else {
            // End this root conversation
            currentConversation.end();
            // Return to the view-id that was captured when this conversation started
            if (endBeforeRedirect)
                browser.redirectToLastBrowsedPage();
            else
                browser.redirectToLastBrowsedPageWithConversation();
        }
    }

    public void diff() {
        System.out.println("#### GENERATING NEW DIFF");

        String revision = ((Document)currentNode).getContent();
        String original = ((Document)selectedHistoricalNode).getContent();

        Diff diff = new Diff() {
            protected String getDeletionStartMarker() {
                return "xXx";
            }

            protected String getDeletionEndMarker() {
                return "XxX";
            }

            protected String getAdditionStartMarker() {
                return "aAa";
            }

            protected String getAdditionEndMarker() {
                return "AaA";
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
            "Rolling back to revision '" + selectedHistoricalNode.getRevision() + "'");
        return "rollback";
    }

    public String getDiffResult() {
        return diffResult;
    }
}
