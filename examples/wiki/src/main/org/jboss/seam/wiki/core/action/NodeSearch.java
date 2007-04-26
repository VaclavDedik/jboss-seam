package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.wiki.core.dao.NodeDAO;
import org.jboss.seam.wiki.core.model.Node;

import java.io.Serializable;
import java.util.List;

@Name("nodeSearch")
@Scope(ScopeType.CONVERSATION)
public class NodeSearch implements Serializable {

    @Logger static Log log;

    @In
    NodeDAO nodeDAO;

    @In
    private FacesMessages facesMessages;

    private String searchTerm = "Search...";

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @DataModel
    private List<Node> searchResult;

    public void search() {
        log.debug("searching nodes for: " + searchTerm);
        searchResult = nodeDAO.search(getSearchTerm());
        log.debug("found nodes: " + searchResult.size());
    }
}
