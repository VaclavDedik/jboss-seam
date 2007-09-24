/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Component;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.search.WikiSearch;
import org.jboss.seam.wiki.util.WikiUtil;

import javax.faces.application.FacesMessage;

/**
 * Returns <tt>docDisplay</tt>, <tt>dirDisplay</tt>, or <tt>search</tt> for the resolved <tt>nodeId</tt>.
 * <p>
 * This resolver expects request parameters in the following format:
 * </p>
 * <pre>
 * http://host/         -- rewrite filter --> http://host/context/wiki.seam
 * http://host/123.html -- rewrite filter --> http://host/context/wiki.seam?nodeId=123
 * http://host/Foo      -- rewrite filter --> http://host/context/wiki.seam?areaName=Foo
 * http://host/Foo/Bar  -- rewrite filter --> http://host/context/wiki.seam?areaName=Foo&nodeName=Bar
 * </pre>
 * <p>
 * 'Foo' is a WikiName of a directory with a parentless parent (ROOT), we call this a logical area.
 * 'Bar' is a WikiName of a node in that logical area, unique within that area subtree.
 * </p>
 * <p>
 * We _never_ have URLs like <tt>http://host/Foo/Baz/Bar</tt> because 'Baz' would be a subdirectory
 * we don't need. An area name and a node name is enough, the node name is unique within
 * a subtree. We also never have <tt>http://host/Bar</tt>, a node name alone is not enough to
 * identify a node, we also need the area name.
 * </p>
 *<p>
 * If the given parameters can't be resolved, the following prodecure applies:
 * </p>
 * <ul>
 * <li> A fulltext search with the supplied area name is attempted, e.g. the request
 *      <tt>http://host/context/wiki.seam?areaName=HelpMe</tt> will result int a wiki fulltext
 *      search for the string "HelpMe"
 * </li>
 * <li>
 *      If the fulltext search did not return any results, the <tt>wikiStart</tt> node is displayed, as
 *      defined in the wiki preferences.
 * </li>
 * </ul>
 * <p>
 * Note that this resolver also sets the identifier and instance on the respetive *Home, e.g. on
 * <tt>documentHome</tt> when <tt>docDisplay</tt> is returned.
 * </p>
 *
 * @author Christian Bauer
 */
@Name("wikiRequestResolver")
@Scope(ScopeType.EVENT)
@AutoCreate
public class WikiRequestResolver {

    @Logger
    static Log log;

    @In
    protected org.jboss.seam.faces.Redirect redirect;

    @In
    private FacesMessages facesMessages;

    @In
    protected NodeDAO nodeDAO;

    protected Long nodeId;
    public Long getNodeId() { return nodeId; }
    public void setNodeId(Long nodeId) { this.nodeId = nodeId; }
    
    protected String areaName;
    public String getAreaName() { return areaName; }
    public void setAreaName(String areaName) { this.areaName = areaName; }

    protected String nodeName;
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }

    protected String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    protected Document currentDocument = null;
    public Document getCurrentDocument() { return currentDocument; }

    protected Directory currentDirectory = null;
    public Directory getCurrentDirectory() { return currentDirectory; }

    public String resolve() {

        // Queue a message if requested (for message passing across session invalidations)
        if (message != null) {
            facesMessages.addFromResourceBundle(
                FacesMessage.SEVERITY_INFO,
                message
            );
        }

        // Have we been called with a nodeId request parameter, could be document or directory
        if (nodeId != null) {
            log.debug("trying to resolve node id: " + nodeId);

            // Try to find a document
            currentDocument = nodeDAO.findDocument(nodeId);

            // Document not found, see if it is a directory
            if (currentDocument == null) {
                currentDirectory = nodeDAO.findDirectory(nodeId);

                // Try to get a default document of that directory
                currentDocument = nodeDAO.findDefaultDocument(currentDirectory);

            } else {
                // Document found, take its directory
                currentDirectory = currentDocument.getParent();
            }

        // Have we been called with an areaName and nodeName request parameter
        } else if (areaName != null && nodeName != null) {
            log.debug("trying to resolve area name: " + areaName + " and node name: " + nodeName);

            // Try to find the area
            Directory area = nodeDAO.findArea(areaName);
            if (area != null) {
                Node node = nodeDAO.findNodeInArea(area.getAreaNumber(), nodeName);
                if (WikiUtil.isDirectory(node)) {
                    currentDirectory = (Directory)node;
                    currentDocument = nodeDAO.findDefaultDocument(currentDirectory);
                 } else {
                    currentDocument = (Document)node;
                    currentDirectory = currentDocument != null ? currentDocument.getParent() : area;
                }
            }

        // Or have we been called just with an areaName request parameter
        } else if (areaName != null) {
            log.debug("trying to resolve area name: " + areaName);
            currentDirectory = nodeDAO.findArea(areaName);
            currentDocument = nodeDAO.findDefaultDocument(currentDirectory);
        }

        log.debug("resolved directory: " + currentDirectory + " and document: " + currentDocument);

        // Fall back, take the area name as a search query
        if (currentDirectory == null) {
            boolean foundMatches = false;
            if (areaName != null && areaName.length() > 0) {
                log.debug("searching for unknown area name: " + areaName);
                WikiSearch wikiSearch = (WikiSearch) Component.getInstance("wikiSearch");
                wikiSearch.setSimpleQuery(areaName);
                wikiSearch.search();
                foundMatches = wikiSearch.getTotalCount() > 0;
            }
            if (foundMatches) {
                log.debug("found search results");
                return "search";
            } else {
                log.debug("falling back to wiki start document");
                // Fall back to default document
                currentDocument = (Document)Component.getInstance("wikiStart");
                currentDirectory = currentDocument.getParent();
            }
        }

        if (currentDocument != null) {
            nodeId = currentDocument.getId();
            DocumentHome documentHome = (DocumentHome)Component.getInstance("documentHome");
            documentHome.setId(nodeId);
            documentHome.setInstance(currentDocument);
            log.debug("displaying document: " + currentDocument);
            return "docDisplay";
        } else {
            nodeId = currentDirectory.getId();
            DirectoryHome directoryHome = (DirectoryHome)Component.getInstance("directoryHome");
            directoryHome.setId(nodeId);
            directoryHome.setInstance(currentDirectory);
            log.debug("displaying directory: " + currentDirectory);
            return "dirDisplay";
        }
    }

}
