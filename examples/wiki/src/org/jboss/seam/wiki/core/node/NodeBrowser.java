package org.jboss.seam.wiki.core.node;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import java.util.*;

/**
 * Resolves <tt>currentDocument</tt> and <tt>currentDirectory</tt> objects for given request parameters.
 * <p>
 * URLs typically mapped and resolved with these classes:
 * <p>
 * <pre>
 * http://host/         -- rewrite filter --> http://host/docDisplay.seam (DONE)
 * http://host/123.html -- rewrite filter --> http://host/docDisplay.seam?nodeId=123 (DONE)
 * http://host/Foo      -- rewrite filter --> http://host/docDisplay.seam?dirName=Foo (PLANNED)
 * http://host/Foo/Bar  -- rewrite filter --> http://host/docDisplay.seam?dirName=Foo&docName=Bar (PLANNED)
 * </pre>
 * 'Foo' is a WikiName of a directory with a parentless parent (ROOT), we call this a logical area.
 * 'Bar' is a WikiName of a document in that logical area, unique within that directory subtree.
 * <p>
 * We _never_ have URLs like <tt>http://host/Foo/Baz/Bar</tt> because 'Baz' would be a subdirectory
 * we don't need. An area name and a document name is enough, the document name is unique within
 * a subtree. We also never have <tt>http://host/Bar</tt>, a document name alone is not enough to
 * identify a document, we also need the area name. In that case, 'Bar' would be treated like an
 * area name and the default document of that area would be shown.
 *
 * @author Christian Bauer
 */
@Name("browser")
public class NodeBrowser {


    @RequestParameter
    protected String dirName;

    @RequestParameter
    protected String docName;

    protected Long nodeId;
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }

    @In(create = true)
    protected EntityManager entityManager;

    @In
    protected org.jboss.seam.core.Redirect redirect;

    @In(create = true)
    protected Directory wikiRoot;

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
/*
        // We don't want to redirect to an action, so if the last browsed page was called with an action, remove it
        redirect.getParameters().remove("actionOutcome");
        redirect.getParameters().remove("actionMethod");

        // If the last browsed page had a conversation identifier (we assume of a temporary conversation), remove it
        redirect.getParameters().remove("cid");

        // We also don't want to redirect the long-running conversation, the caller has ended it already
        redirect.setConversationPropagationEnabled(false);
*/
        redirect.execute();
    }

    // Just a convenience method for recursive calling
    protected void addDirectoryToPath(List<Node> path, Node directory) {
        path.add(directory);
        if (directory.getParent() != null )
            addDirectoryToPath(path, directory.getParent());
    }

    public String prepareAndCapture() {
        // Store the view-id that called this method (as a page action) for return (exit of a later conversation)
        redirect.captureCurrentRequest();
        return prepare();
    }

    @Transactional
    public String prepare() {

        // Have we been called with a nodeId request parameter, could be document or directory
        if (nodeId != null) {

            entityManager.joinTransaction();

            // Try to find a document
            try {
                currentDocument = entityManager.find(Document.class, nodeId);
            } catch (EntityNotFoundException ex) {}

            // Document not found, see if it is a directory
            if (currentDocument == null) {
                try {
                    currentDirectory = entityManager.find(Directory.class, nodeId);
                } catch (EntityNotFoundException ex) {}

                // Try to get a default document of that directory
                if (currentDirectory != null) {
                    currentDocument = currentDirectory.getDefaultDocument();
                } else {
                }
            } else {
                // Document found, take its directory
                currentDirectory = currentDocument.getParent();
            }
        }

        // Fall back to wiki root
        if (currentDirectory== null) currentDirectory = wikiRoot;

        // Prepare directory path for breadcrumb
        addDirectoryToPath(currentDirectoryPath, currentDirectory);
        Collections.reverse(currentDirectoryPath);

        // This handles the wiki names in dirName and docName request parameters.
        // The logic here is the same as the code that will resolve wiki URLs during rendering of
        // pages, so we need to do that later...
        if (dirName != null) {
            System.out.println("#### NEED TO RESOLVE DIR NAME: " + dirName);
        }
        if (docName != null) {
            System.out.println("#### NEED TO RESOLVE DOC NAME: " + docName);
        }

        // Return not-null outcome so we can navigate from here
        return "prepared";
    }


}
