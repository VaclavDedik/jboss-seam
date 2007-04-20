package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.AuthorizationException;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.engine.WikiLink;
import org.jboss.seam.wiki.core.engine.WikiTextParser;
import org.jboss.seam.wiki.core.engine.WikiTextRenderer;
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

    private String diffResult;

    @Factory("historicalNodeList")
    public void initializeHistoricalNodeList() {
        if (historicalNodeList == null)
            historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);
    }

    @Create
    public void create() {
        if (!Identity.instance().hasPermission("Node", "read", currentNode) ) {
            throw new AuthorizationException("You don't have permission for this operation");
        }

        historicalNodeList = nodeDAO.findHistoricalNodes(currentNode);

        if (historicalNodeList.size() == 0) {
            facesMessages.addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "noHistory",
                "No stored history for this document, you are looking at the only existing revision.");
            NodeBrowser browser = (NodeBrowser) Component.getInstance("browser");
            browser.exitConversation(false);
        }
    }

    public void diff() {

        // Wiki text parser needs these context variables but we don't really care because link resolving is turned off
        Contexts.getConversationContext().set("currentDocument", currentNode);
        Contexts.getConversationContext().set("currentDirectory", currentNode.getParent());
        String revision = renderWikiText( ((Document)currentNode).getContent() );
        Contexts.getConversationContext().set("currentDocument", selectedHistoricalNode);
        Contexts.getConversationContext().set("currentDirectory", currentNode.getParent());
        String original = renderWikiText( ((Document)selectedHistoricalNode).getContent() );

        // Create diff by comparing rendered HTML
        Diff diff = new Diff() {
            protected String getDeletionStartMarker() {
                return "<div class=\"diffDeleted\">";
            }

            protected String getDeletionEndMarker() {
                return "</div>";
            }

            protected String getAdditionStartMarker() {
                return "<div class=\"diffAdded\">";
            }

            protected String getAdditionEndMarker() {
                return "</div>";
            }
        };

        // Diff is line-based only
        String[] x = original.split("\r\n");
        String[] y = revision.split("\r\n");
        String[] result = diff.createDiff(x, y, "\r\n", "\r", "\n");

        // Now render the combined string array
        diffResult = Diff.renderWithDelimiter(result, "\r\n");

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

    private String renderWikiText(String wikiText) {
        // Render the document to HTML for diff, don't resolve any wiki links (calls renderInlineLink() plain)
        WikiTextParser parser = new WikiTextParser(wikiText, true, false);
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
