package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiFile;

import java.io.Serializable;
import java.util.List;

@Name("tagQuery")
@Scope(ScopeType.CONVERSATION)
public class TagQuery implements Serializable {

    @In
    TagDAO tagDAO;

    @In
    WikiDirectory wikiRoot;

    private String tag;
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    private List<WikiFile> taggedFiles;

    public List<WikiFile> getTaggedFiles() {
        if (taggedFiles == null) {
            loadTaggedFiles();
        }
        return taggedFiles;
    }

    public void loadTaggedFiles() {
        taggedFiles = tagDAO.findWikFiles(wikiRoot, null, tag);
    }
}
