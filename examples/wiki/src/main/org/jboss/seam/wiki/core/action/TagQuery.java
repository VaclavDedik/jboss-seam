/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.action;

import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.wiki.core.dao.TagDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.exception.InvalidWikiRequestException;

import java.io.Serializable;
import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("tagQuery")
@Scope(ScopeType.CONVERSATION)
public class TagQuery implements Serializable {

    @Logger
    Log log;

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
        if (tag == null) {
            throw new InvalidWikiRequestException("Missing tag parameter");
        }
        log.debug("loading wiki files tagged with: " + tag);
        taggedFiles = tagDAO.findWikFiles(wikiRoot, null, tag, WikiNode.SortableProperty.createdOn, false);
    }
}
