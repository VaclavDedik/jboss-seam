package org.jboss.seam.wiki.plugin.faqBrowser;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Events;
import org.jboss.seam.international.Messages;
import org.jboss.seam.wiki.core.action.DocumentHome;
import org.jboss.seam.wiki.core.model.WikiDocument;
import org.jboss.seam.wiki.core.model.WikiDocumentDefaults;

import static javax.faces.application.FacesMessage.SEVERITY_INFO;

@Name("faqQuestionHome")
@Scope(ScopeType.CONVERSATION)
public class FaqQuestionHome extends DocumentHome {

    @In
    FaqBrowser faqBrowser;

    private boolean showForm = false;

    /* -------------------------- Basic Overrides ------------------------------ */

    @Override
    protected boolean isPageRootController() {
        return false;
    }

    @Override
    public Class<WikiDocument> getEntityClass() {
        return WikiDocument.class;
    }

    @Override
    public void create() {
        super.create();
        setParentNodeId(faqBrowser.getSelectedDir().getWrappedNode().getId());
    }

    @Override
    public WikiDocument afterNodeCreated(WikiDocument doc) {
        WikiDocument newQuestion = super.afterNodeCreated(doc);

        WikiDocumentDefaults newQuestionDefaults=
                new WikiDocumentDefaults() {
                    @Override
                    public String getName() {
                        return Messages.instance().get("faqBrowser.label.NewQuestionTitle");
                    }
                    @Override
                    public String[] getHeaderMacrosAsString() {
                        return new String[] { "faqBrowser", "docPager" };
                    }
                    @Override
                    public String getContentText() {
                        return Messages.instance().get("lacewiki.msg.wikiTextEditor.EditThisText");
                    }
                    @Override
                    public void setOptions(WikiDocument newQuestion) {
                        newQuestion.setNameAsTitle(true);
                        newQuestion.setEnableComments(true);
                        newQuestion.setEnableCommentForm(true);
                        newQuestion.setEnableCommentsOnFeeds(false);
                    }
                };
        newQuestion.setDefaults(newQuestionDefaults);

        return newQuestion;
    }

    @Override
    public String persist() {
        String outcome = super.persist();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    @Override
    public String update() {
        String outcome = super.update();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    @Override
    public String remove() {
        String outcome = super.remove();
        if (outcome != null) endConversation();
        return null; // Prevent navigation
    }

    /* -------------------------- Messages ------------------------------ */

    protected void createdMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "faqBrowser.msg.Question.Persist",
                "Question '{0}' has been saved.",
                getInstance().getName()
        );
    }

    protected void updatedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "faqBrowser.msg.Question.Update",
                "Question '{0}' has been updated.",
                getInstance().getName()
        );
    }

    protected void deletedMessage() {
        getFacesMessages().addFromResourceBundleOrDefault(
                SEVERITY_INFO,
                "faqBrowser.msg.Question.Delete",
                "Question '{0}' has been deleted.",
                getInstance().getName()
        );
    }

    /* -------------------------- Internal Methods ------------------------------ */

    private void endConversation() {
        getLog().debug("ending conversation and hiding question form");
        showForm = false;
        Conversation.instance().end();
        getEntityManager().clear(); // Need to force re-read in the question list refresh
        Events.instance().raiseEvent("FaqBrowser.questionListRefresh");
    }

    /* -------------------------- Public Features ------------------------------ */

    public boolean isShowForm() {
        return showForm;
    }

    public void setShowForm(boolean showForm) {
        this.showForm = showForm;
    }

    @Begin(flushMode = FlushModeType.MANUAL, join = true)
    public void newQuestion() {
        initEditor();
        showForm = true;
    }

    public void cancel() {
        endConversation();
    }

}