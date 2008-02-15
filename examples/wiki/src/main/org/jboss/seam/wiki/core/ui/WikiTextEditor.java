package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.remoting.WebRemote;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.ScopeType;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.ui.validator.FormattedTextValidator;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;

/**
 * Store UI status of wiki text editor.
 * <p>
 *
 * @author Christian Bauer
 */
@Name("wikiTextEditor")
@Scope(ScopeType.CONVERSATION) // TODO: Should be PAGE but doesn't work with Seam remoting!
public class WikiTextEditor implements Serializable {

    public void validate(String textEditorId, String value) {
        if (value == null) return;
        FormattedTextValidator validator = new FormattedTextValidator();
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
