package org.jboss.seam.wiki.core.node;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.dao.NodeDAO;

import java.util.*;

/**
 * Resolves <tt>currentDocument</tt> and <tt>currentDirectory</tt> objects for given request parameters.
 * <p>
 * URLs typically mapped and resolved with these classes:
 * <p>
 * <pre>
 * http://host/         -- rewrite filter --> http://host/context/display.seam
 * http://host/123.html -- rewrite filter --> http://host/context/display.seam?nodeId=123
 * http://host/Foo      -- rewrite filter --> http://host/context/display.seam?areaName=Foo
 * http://host/Foo/Bar  -- rewrite filter --> http://host/context/display.seam?areaName=Foo&nodeName=Bar
 * </pre>
 * 'Foo' is a WikiName of a directory with a parentless parent (ROOT), we call this a logical area.
 * 'Bar' is a WikiName of a node in that logical area, unique within that area subtree.
 * <p>
 * We _never_ have URLs like <tt>http://host/Foo/Baz/Bar</tt> because 'Baz' would be a subdirectory
 * we don't need. An area name and a node name is enough, the node name is unique within
 * a subtree. We also never have <tt>http://host/Bar</tt>, a node name alone is not enough to
 * identify a node, we also need the area name.
 *
 * @author Christian Bauer
 */
@Name("browser")
@Scope(ScopeType.EVENT)
public class NodeBrowser {

    @RequestParameter
    protected String areaName;

    @RequestParameter
    protected String nodeName;

    protected Long nodeId;
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    @In
    protected org.jboss.seam.core.Redirect redirect;

    @In(create = true)
    protected Directory wikiRoot;

    @In(create = true)
    protected NodeDAO nodeDAO;

    // These are only EVENT scoped, we don't want them to jump from DocumentBrowser to
    // DirectoryBrowser over redirects
    @In(required=false) @Out(scope = ScopeType.EVENT, required = false)
    protected Document currentDocument;

    @In(required=false) @Out(scope = ScopeType.EVENT, required = false)
    protected Directory currentDirectory;

    @Out(scope = ScopeType.EVENT)
    protected  List<Node> currentDirectoryPath = new ArrayList<Node>();


    /**
     * Executes a redirect to the last view-id that was prepare()ed.
     * <p>
     * Usually called after ending a conversation. Assumes that the caller of the method does not want to propagate
     * the current (ended) conversation across that redirect. Also removes any stored <tt>actionOutcome</tt>,
     * <tt>actionMethod</tt> or <tt>cid</tt> request parameter before redirecting, we don't want to redirect to
     * a prepare()ed page that was in a long-running conversation (temporary doesn't matter) or that was last
     * called with an action (that action would probably send us straight back into the conversation we are trying
     * to redirect out of).
     */
    public void redirectToLastBrowsedPage() {

        // We don't want to redirect to an action, so if the last browsed page was called with an action, remove it
        redirect.getParameters().remove("actionOutcome");
        redirect.getParameters().remove("actionMethod");

        // If the last browsed page had a conversation identifier (we assume of a temporary conversation), remove it
        redirect.getParameters().remove("cid");

        // We also don't want to redirect the long-running conversation, the caller has ended it already
        redirect.setConversationPropagationEnabled(false);

        redirect.returnToCapturedView();
    }

    public void redirectToLastBrowsedPageWithConversation() {
        // We don't want to redirect to an action, so if the last browsed page was called with an action, remove it
        redirect.getParameters().remove("actionOutcome");
        redirect.getParameters().remove("actionMethod");

        // If the last browsed page had a conversation identifier (we assume of a temporary conversation), remove it
        redirect.getParameters().remove("cid");

        redirect.returnToCapturedView();
    }


    // Just a convenience method for recursive calling
    protected void addDirectoryToPath(List<Node> path, Node directory) {
        path.add(directory);
        if (directory.getParent() != null )
            addDirectoryToPath(path, directory.getParent());
    }

    @Transactional
    public String prepare() {

        // Store the view-id that called this method (as a page action) for return (exit of a later conversation)
        redirect.captureCurrentRequest();
        // TODO: I'm not using captureCurrentView() because it starts a conversation

        // Have we been called with a nodeId request parameter, could be document or directory
        if (nodeId != null && !nodeId.equals(wikiRoot.getId())) {

            // Try to find a document
            currentDocument = nodeDAO.findDocument(nodeId);

            // Document not found, see if it is a directory
            if (currentDocument == null) {
                currentDirectory = nodeDAO.findDirectory(nodeId);

                // Try to get a default document of that directory
                if (currentDirectory != null) currentDocument = currentDirectory.getDefaultDocument();

            } else {
                // Document found, take its directory
                currentDirectory = currentDocument.getParent();
            }

        // Have we been called with an areaName and nodeName request parameter
        } else if (areaName != null && nodeName != null) {

            // Try to find the area
            Directory area = nodeDAO.findArea(areaName);
            if (area != null) {
                Node node = nodeDAO.findNodeInArea(area.getAreaNumber(), nodeName);
                if (isDirectory(node)) {
                    currentDirectory = (Directory)node;
                    currentDocument = currentDirectory.getDefaultDocument();
                 } else {
                    currentDocument = (Document)node;
                    currentDirectory = currentDocument != null ? currentDocument.getParent() : area;
                }
            }

        // Or have we been called just with an areaName request parameter
        } else if (areaName != null) {
            currentDirectory = nodeDAO.findArea(areaName);
            if (currentDirectory != null) currentDocument = currentDirectory.getDefaultDocument();
        }

        // Fall back to wiki root
        if (currentDirectory== null) currentDirectory = wikiRoot;

        // Set the id for later
        nodeId = currentDocument != null ? currentDocument.getId() : currentDirectory.getId();

        // Prepare directory path for breadcrumb
        addDirectoryToPath(currentDirectoryPath, currentDirectory);
        Collections.reverse(currentDirectoryPath);

        // Return not-null outcome so we can navigate from here
        return "prepared";
    }

    // Replacement for missing instaceOf in EL (can't use string comparison, might be proxy)
    public static boolean isDirectory(Node node) {
        return node != null && Directory.class.isAssignableFrom(node.getClass());
    }

    public static boolean isDocument(Node node) {
        return node != null && Document.class.isAssignableFrom(node.getClass());
    }

}
