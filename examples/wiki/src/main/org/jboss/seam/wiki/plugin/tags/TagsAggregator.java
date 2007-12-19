package org.jboss.seam.wiki.plugin.tags;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.DisplayTagCount;
import org.jboss.seam.wiki.core.dao.TagDAO;

import java.util.*;
import java.io.Serializable;

@Name("tagsAggregator")
@Scope(ScopeType.PAGE)
public class TagsAggregator implements Serializable {

    @DataModel
    List<DisplayTagCount> tagsSortedByCount = new ArrayList<DisplayTagCount>();

    @In
    TagDAO tagDAO;

    @In
    WikiDirectory currentDirectory;

    @In
    WikiDocument currentDocument;

    @Factory("tagsSortedByCount")
    public void aggregateTags() {
        tagsSortedByCount = tagDAO.findTagCounts(currentDirectory, currentDocument, 0);
    }

}
