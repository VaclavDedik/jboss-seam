package org.jboss.seam.wiki.plugin.tags;

import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.Observer;
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

    @In("#{preferences.get('Tags', currentMacro)}")
    TagsPreferences prefs;

    @Factory("tagsSortedByCount")
    @Observer(value = "Macro.render.tags", create = false)
    public void aggregateTags() {
        tagsSortedByCount =
                tagDAO.findTagCounts(
                        currentDirectory,
                        currentDocument,
                        prefs.getMaxNumberOfTags() != null ? prefs.getMaxNumberOfTags().intValue() : 0,
                        prefs.getMinimumCount() != null ? prefs.getMinimumCount() : 1l
                );
        for (DisplayTagCount tagCount : tagsSortedByCount) {
            if (tagCount.getCount() < lowestTagCount) lowestTagCount = tagCount.getCount();
            if (tagCount.getCount() > highestTagCount) highestTagCount= tagCount.getCount();
        }
    }

    private long highestTagCount = 0l;
    private long lowestTagCount = 0l;
    public long getHighestTagCount() {
        return highestTagCount;
    }

    public long getLowestTagCount() {
        return lowestTagCount;
    }

    
}
