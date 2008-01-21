/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.plugin.jiraIssueList;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.connectors.jira.JiraIssue;
import org.jboss.seam.wiki.connectors.jira.JiraDAO;

import java.util.List;
import java.io.Serializable;

/**
 * @author Christian Bauer
 */
@Name("jiraIssueList")
@Scope(ScopeType.PAGE)
public class JiraIssueList implements Serializable {

    @In("#{preferences.get('JiraIssueList', currentMacro)}")
    JiraIssueListPreferences prefs;

    @In
    JiraDAO jiraDAO;

    private List<JiraIssue> issues;

    public List<JiraIssue> getIssues() {
        if (issues == null) loadIssues();
        return issues;
    }

    @Observer(value = "Macro.render.jiraIssueList", create = false)
    public void loadIssues() {

        if (prefs.getUrl() != null && prefs.getUrl().length() > 0
            && prefs.getFilterId() != null && prefs.getFilterId().length() > 0) {

            Integer maxResults = Integer.MAX_VALUE;
            if (prefs.getNumberOfIssues() != null) {
                maxResults = prefs.getNumberOfIssues().intValue();
            }

            issues = jiraDAO.getJiraIssues(prefs.getUrl(), prefs.getUsername(), prefs.getPassword(), prefs.getFilterId(), maxResults);
        }

    }
}
