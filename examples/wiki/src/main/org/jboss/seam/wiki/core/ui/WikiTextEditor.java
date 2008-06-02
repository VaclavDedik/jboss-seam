package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.wiki.core.engine.WikiFormattedTextValidator;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;

/**
 * Store UI status of wiki text editor.
 * <p>
 * This is called via Javascript when the user changes some visual properties of the wiki text editor,
 * atm this is only resizing of the text area. We need to store the UI properties so that we can restore
 * the UI when the user exits the page and comes back later in the conversation. Or, when we reRender the
 * wiki text editor we need to apply these properties so it looks the same as before the reRender.
 *
 * @author Christian Bauer
 */
@Name("wikiTextEditor")
@Scope(ScopeType.CONVERSATION) // TODO: Should be PAGE but doesn't work with Seam remoting!
public class WikiTextEditor implements Serializable {

    private Map<String, String> textAreaRows = new HashMap<String, String>();

    @WebRemote
    public void setTextAreaRows(String editorId, String textAreaRows) {
        this.textAreaRows.put(editorId, textAreaRows);
    }

    public String getTextAreaRows(String editorId) {
        return textAreaRows.get(editorId);
    }

    public void validate(String textEditorId, String value) {
        if (value == null) return;

        WikiFormattedTextValidator validator = new WikiFormattedTextValidator();

        try {
            validator.validate(null, null, value);
        } catch (ValidatorException e) {
            // TODO: Needs to use resource bundle, how?
            FacesMessages.instance().addToControl(
                textEditorId + "TextArea",
                FacesMessage.SEVERITY_WARN,
                e.getFacesMessage().getSummary()
            );
        }

    }
}
