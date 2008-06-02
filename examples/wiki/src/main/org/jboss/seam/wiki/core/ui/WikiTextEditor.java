package org.jboss.seam.wiki.core.ui;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.wiki.core.engine.WikiFormattedTextValidator;
import org.jboss.seam.international.StatusMessages;
import org.jboss.seam.log.Log;

import static org.jboss.seam.international.StatusMessage.Severity.WARN;

import java.io.Serializable;

import javax.faces.validator.ValidatorException;

/**
 * Utility class bound to Wiki text editor UI.
 * <p>
 *
 * @author Christian Bauer
 */
@Name("wikiTextEditor")
@Scope(ScopeType.CONVERSATION)
public class WikiTextEditor implements Serializable {

    @Logger
    Log log;

    public void validate(String textEditorId, String value) {
        if (value == null) return;
        log.debug("validating value of text editor: " + textEditorId);
        WikiFormattedTextValidator validator = new WikiFormattedTextValidator();
        try {
            validator.validate(null, null, value);
        } catch (ValidatorException e) {
            log.debug("exception during validation: " + e.getFacesMessage().getSummary());
            StatusMessages.instance().addToControl(
                textEditorId + "TextArea",
                WARN,
                e.getFacesMessage().getSummary()
            );
        }
        log.debug("completed validation of text editor value");

    }
}
