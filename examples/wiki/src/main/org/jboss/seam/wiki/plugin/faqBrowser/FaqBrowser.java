/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.web.RequestParameter;
import org.jboss.seam.log.Log;
import org.jboss.seam.wiki.core.dao.WikiNodeDAO;
import org.jboss.seam.wiki.core.model.WikiDirectory;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiNode;
import org.jboss.seam.wiki.core.nestedset.query.NestedSetNodeWrapper;

import java.util.List;

/**
 * @author Christian Bauer
 */
@Name("faqBrowser")
@Scope(ScopeType.PAGE)
@AutoCreate
public class FaqBrowser {

    @Logger
    Log log;

    @In
    FaqBrowserDAO faqBrowserDAO;

    @In
    WikiNodeDAO wikiNodeDAO;

    @In
    WikiDirectory currentDirectory;

    NestedSetNodeWrapper<WikiDirectory> tree;

    public NestedSetNodeWrapper<WikiDirectory> getTree() {
        if (tree == null) loadTree();
        return tree;
    }

    public void loadTree() {
        log.debug("loading faq root, starting search for parent default file with macro in directory: " + currentDirectory);
        WikiDirectory faqRoot = faqBrowserDAO.findFaqRootDir(currentDirectory);
        if (faqRoot != null) {
            log.debug("found faq root: " + faqRoot);
            tree = wikiNodeDAO.findWikiDirectoryTree(faqRoot, 99l, 1l, false);
        } else {
            log.debug("couldn't find faq tree root by searching the directory tree upwards, assuming that the current dir is the root");
            tree = wikiNodeDAO.findWikiDirectoryTree(currentDirectory, 99l, 1l, false);
        }
    }

    NestedSetNodeWrapper<WikiDirectory> selectedDir;
    boolean directorySelected = false;

    @Create
    public void setDefaultDir() {
        selectedDir = new NestedSetNodeWrapper<WikiDirectory>(currentDirectory);
    }

    public NestedSetNodeWrapper<WikiDirectory> getSelectedDir() {
        return selectedDir;
    }

    public void setSelectedDir(NestedSetNodeWrapper<WikiDirectory> selectedDir) {
        this.selectedDir = selectedDir;
    }

    public boolean isDirectorySelected() {
        return directorySelected;
    }

    @Observer("FaqBrowser.questionListRefresh")
    public void showQuestions() {
        log.debug("showing questions of currently selected directory: " + selectedDir.getWrappedNode());
        directorySelected = true;
        questions = wikiNodeDAO.findWikiDocuments(selectedDir.getWrappedNode(), WikiNode.SortableProperty.createdOn, true);
    }

    public void hideQuestions() {
        log.debug("hiding questions");
        directorySelected = false;
        this.questions = null;
    }

    List<WikiDocument> questions;

    public List<WikiDocument> getQuestions() {
        return questions;
    }

    @RequestParameter("category")
    public void selectCategory(String requestParam) {
        if (requestParam != null && requestParam.length() > 0) {
            WikiDirectory category = wikiNodeDAO.findWikiDirectoryInArea(getTree().getWrappedNode().getAreaNumber(), requestParam);
            if (category != null) {
                selectedDir = new NestedSetNodeWrapper<WikiDirectory>(category);
                showQuestions();
            }

        }
    }

}
