package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;
import org.apache.lucene.queryParser.ParseException;

import javax.faces.application.FacesMessage;
import java.io.Serializable;
import java.util.List;

@Name("nodeSearch")
@Scope(ScopeType.CONVERSATION)
public class NodeSearch implements Serializable {

    @Logger static Log log;

    @In
    private FacesMessages facesMessages;

    @In
    NodeDAO nodeDAO;

    private String query = "Search...";

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @DataModel
    List<Node> searchResult;

    @Factory("searchResult")
    public void  search() {
        log.debug("searching nodes for: " + query);
        try {
            searchResult = nodeDAO.search(query);
        } catch (ParseException e) {
            facesMessages.addFromResourceBundleOrDefault(
                FacesMessage.SEVERITY_INFO,
                "illegalSearchTerm",
                "Your search query has invalid syntax, please try again" + (log.isDebugEnabled() ? e.getMessage() : null) );
        }
        log.debug("found nodes: " + searchResult.size());
    }
}
