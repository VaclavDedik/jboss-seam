package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
import org.jboss.seam.wiki.core.engine.WikiLinkResolver;
import org.jboss.seam.wiki.util.Diff;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;

/**
 * Diff for historical documents.
 *
 * TODO: Only supports documents, name implies that it should support other stuff.
 *
 * @author Christian Bauer
 */
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
    private Document selectedHistoricalNode;

    Long nodeId;
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    Long historicalNodeId;
    public Long getHistoricalNodeId() { return historicalNodeId; }
    public void setHistoricalNodeId(Long historicalNodeId) { this.historicalNodeId = historicalNodeId; }

    private Document currentNode;
    public Node getCurrentNode() { return currentNode;    }

    private Document displayedHistoricalNode;
    public Document getDisplayedHistoricalNode() { return displayedHistoricalNode; }

    private String diffResult;
    public String getDiffResult() { return diffResult; }

    @Factory("historicalNodeList")
    public void initializeHistoricalNodeList() {
        if (historicalNodeList == null)
            historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
    }

    public String init() {
        if (nodeId == null) return "missingParameter";

        if (currentNode == null) {
            currentNode = nodeDAO.findDocument(nodeId);
            if (!Identity.instance().hasPermission("Node", "read", currentNode) ) {
                throw new AuthorizationException("You don't have permission for this operation");
            }
        }

        if (historicalNodeList == null) {
            historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
        }
        return null;
    }

    public void displayHistoricalRevision() {
        displayedHistoricalNode = selectedHistoricalNode;
        diffResult = null;

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "diffOldVersionDisplayed",
            "Showing historical revision " + selectedHistoricalNode.getRevision());
    }

    public void diff() {
        init(); // TODO: Why doesn't Seam execute my page action but instead s:link action="diff" in a fake RENDER RESPONSE?!?
        displayedHistoricalNode = null;

        if (historicalNodeId == null) return;
        selectedHistoricalNode = (Document)nodeDAO.findHistoricalNode(historicalNodeId);
        if (selectedHistoricalNode == null) {
            facesMessages.addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_ERROR,
                "historicalNodeNotFound",
                "Couldn't find historical node: " + historicalNodeId);
            return;
        }

        String[] a = selectedHistoricalNode.getContent().split("\n");
        String[] b = currentNode.getContent().split("\n");

        StringBuilder result = new StringBuilder();
        List<Diff.Difference> differences = new Diff(a, b).diff();

        for (Diff.Difference diff : differences) {
            int        delStart = diff.getDeletedStart();
            int        delEnd   = diff.getDeletedEnd();
            int        addStart = diff.getAddedStart();
            int        addEnd   = diff.getAddedEnd();
            String     type     = delEnd != Diff.NONE && addEnd != Diff.NONE ? "changed" : (delEnd == Diff.NONE ? "added" : "deleted");

            // Info line
            result.append("<div class=\"diffInfo\">");
            result.append("From ");
            result.append(delStart == delEnd || delEnd == Diff.NONE ? "line" : "lines");
            result.append(" ");
            result.append(delStart);
            if (delEnd != Diff.NONE && delStart != delEnd) {
                result.append(" to ").append(delEnd);
            }
            result.append(" ").append(type).append(" to ");
            result.append(addStart == addEnd || addEnd == Diff.NONE ? "line" : "lines");
            result.append(" ");
            result.append(addStart);
            if (addEnd != Diff.NONE && addStart != addEnd) {
                result.append(" to ").append(addEnd);
            }
            result.append(":");
            result.append("</div>\n");

            if (delEnd != Diff.NONE) {
                result.append("<div class=\"diffDeleted\">");
                for (int lnum = delStart; lnum <= delEnd; ++lnum) {
                    result.append( WikiUtil.escapeHtml(a[lnum], false) ).append("<br/>");
                }
                result.append("</div>");
                if (addEnd != Diff.NONE) {
                    //result.append("----------------------------").append("\n");
                }
            }
            if (addEnd != Diff.NONE) {
                result.append("<div class=\"diffAdded\">");
                for (int lnum = addStart; lnum <= addEnd; ++lnum) {
                    result.append( WikiUtil.escapeHtml(b[lnum], false) ).append("<br/>");
                }
                result.append("</div>");
            }
        }

        diffResult = result.toString();

        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "diffCreated",
            "Comparing current revision with historical revision " + selectedHistoricalNode.getRevision());
    }

    @Restrict("#{s:hasPermission('Node', 'edit', nodeHistory.currentNode)}")
    public String rollback() {
        facesMessages.addFromResourceBundleOrDefault(
            FacesMessage.SEVERITY_INFO,
            "rollingBackDocument",
            "Rolling back to revision " + selectedHistoricalNode.getRevision());
        return "rollback";
    }

    @Restrict("#{s:hasPermission('User', 'isAdmin', currentUser)}")
    public String purgeHistory() {
        nodeDAO.removeHistoricalNodes(getCurrentNode());
        return "purgedHistory";
    }

}
