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

    Long nodeId;

    public Long getNodeId() {
        return nodeId;
    }

    public void setNodeId(Long nodeId) {
        this.nodeId = nodeId;
    }

    @DataModelSelection
    @Out(required = false, scope = ScopeType.CONVERSATION)
    private Document selectedHistoricalNode;

    @In(required = false) @Out(required = false, scope = ScopeType.CONVERSATION)
    private Document currentNode;

    private Document displayedHistoricalNode;
    public Document getDisplayedHistoricalNode() {
        return displayedHistoricalNode;
    }

    private String diffResult;

    @Factory("historicalNodeList")
    public void initializeHistoricalNodeList() {
        if (historicalNodeList == null)
            historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
    }

    public String init() {
        if (nodeId == null) return "missingParameter";

        currentNode = nodeDAO.findDocument(nodeId);
        if (!Identity.instance().hasPermission("Node", "read", currentNode) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }
        historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
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
        displayedHistoricalNode = null;

        // Wiki text parser needs these nodes but we don't really care because links are not rendered and resolved
        String revision = renderWikiText( currentNode, currentNode.getParent(), currentNode.getContent() );
        String original = renderWikiText( selectedHistoricalNode, currentNode.getParent(), selectedHistoricalNode.getContent() );

        String[] a = original.split("\n");
        String[] b = revision.split("\n");
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
                    result.append(a[lnum]);
                }
                result.append("</div>");
                if (addEnd != Diff.NONE) {
                    //result.append("----------------------------").append("\n");
                }
            }
            if (addEnd != Diff.NONE) {
                result.append("<div class=\"diffAdded\">");
                for (int lnum = addStart; lnum <= addEnd; ++lnum) {
                    result.append(b[lnum]);
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

    @Restrict("#{s:hasPermission('Node', 'edit', currentNode)}")
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

    private String renderWikiText(Document currentDocument, Directory currentDirectory, String wikiText) {
        // Render the document to HTML for diff, don't resolve any wiki links (calls renderInlineLink() plain)
        WikiTextParser parser = new WikiTextParser(wikiText, true, false);

        parser.setCurrentDocument(currentDocument);
        parser.setCurrentDirectory(currentDirectory);

        parser.setResolver((WikiLinkResolver)Component.getInstance("wikiLinkResolver"));
        
        // This renderer is really just ignoring everything and renders a few placeholders
        parser.setRenderer(
            new WikiTextRenderer() {
                public String renderInlineLink(WikiLink inlineLink) {
                    return "<span class=\"diffLink\">[" + inlineLink.getDescription() + "=>" + inlineLink.getUrl() + "]</span>";
                }
                public String renderExternalLink(WikiLink externalLink) { return null; }
                public String renderFileAttachmentLink(int attachmentNumber, WikiLink attachmentLink) { return null; }
                public String renderThumbnailImageInlineLink(WikiLink inlineLink) { return null; }
                public String renderMacro(String macroName) {
                    return "<span class=\"diffPlugin\">Plugin: " + macroName + "</span>";
                }
                public void setAttachmentLinks(List<WikiLink> attachmentLinks) {}
                public void setExternalLinks(List<WikiLink> externalLinks) {}
            }
        );

        // Run the parser
        parser.parse(false);
        return parser.toString();
    }

}
