package org.jboss.seam.wiki.plugin.tags;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.model.Document;
import org.jboss.seam.wiki.core.model.Directory;
import org.jboss.seam.wiki.core.dao.TagDAO;

import java.util.*;
import java.io.Serializable;

@Name("tagsAggregator")
@Scope(ScopeType.PAGE)
public class TagsAggregator implements Serializable {

    @DataModel
    List<TagDAO.TagCount> tagsSortedByCount = new ArrayList<TagDAO.TagCount>();

    @In
    TagDAO tagDAO;

    @In
    Directory currentDirectory;

    @In
    Document currentDocument;

    @Factory("tagsSortedByCount")
    public void aggregateTags() {
        tagsSortedByCount = tagDAO.findTagsAggregatedSorted(currentDirectory, currentDocument, 0);
    }

}
