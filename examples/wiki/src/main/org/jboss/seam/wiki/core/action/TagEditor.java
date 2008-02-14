/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.model.DisplayTagCount;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiFile;

import javax.faces.application.FacesMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("tagEditor")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class TagEditor implements Serializable {

    @In
    private TagDAO tagDAO;

    @In
    private WikiDirectory wikiRoot;

    @In
    private FacesMessages facesMessages;

    private SortedSet<String> tags;
    private String newTag;
    private List<DisplayTagCount> popularTags;

    public SortedSet<String> getTags() {
        return tags;
    }

    public void setTags(SortedSet<String> tags) {
        this.tags = tags;
    }

    public List<String> getTagsAsList() {
        return new ArrayList<String>(tags);
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }

    public void removeTag(String tag) {
        tags.remove(tag);
    }

    public void addTag(String tag) {
        tags.add(tag);
    }

    public void addNewTag() {
        if (newTag.contains("&")) {
            facesMessages.addToControlFromResourceBundleOrDefault(
                "newTag",
                FacesMessage.SEVERITY_WARN,
                "lacewiki.msg.tagEdit.TagCantContainAmpersand",
                "Tag can not contain an ampersand."
            );
        } else if (newTag.length() > 0) {
            tags.add(newTag);
            newTag = null;
        }
    }

    public List<DisplayTagCount> getPopularTags() {
        // Load 6 most popular tags
        if (popularTags == null) popularTags = tagDAO.findTagCounts(wikiRoot, null, 6, 1l);

        // Filter out the ones we already have
        List<DisplayTagCount> filtered = new ArrayList<DisplayTagCount>();
        for (DisplayTagCount popularTag : popularTags) {
            if (!tags.contains(popularTag.getTag()))
                filtered.add(popularTag);
        }
        return filtered;
    }

}
