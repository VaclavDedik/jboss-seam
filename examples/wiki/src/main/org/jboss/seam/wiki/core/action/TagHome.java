package org.jboss.seam.wiki.core.action;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.model.Node;
import org.jboss.seam.wiki.core.dao.TagDAO;

import java.io.Serializable;
import java.util.List;

@Name("tagHome")
@Scope(ScopeType.PAGE)
public class TagHome implements Serializable {

    @In
    TagDAO tagDAO;

    @In
    Directory wikiRoot;

    private String tag;
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    private List<Node> taggedDocuments;

    public List<Node> getTaggedDocuments() {
        if (taggedDocuments == null) {
            loadTaggedDocuments();
        }
        return taggedDocuments;
    }

    public void loadTaggedDocuments() {
        taggedDocuments = tagDAO.findNodes(wikiRoot, null, tag);
    }
}
