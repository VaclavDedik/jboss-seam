package org.jboss.seam.wiki.core.wikitext.editor;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;

/**
 * Wraps the <tt>WikiFormattedTextValidator</tt> into a conversation-scoped action
 * that queues the right messages and holds the status of various text editors
 * for easy error checking by the user interface.
 *
 * @see WikiFormattedTextValidator
 *
 * @author Christian Bauer
 */
@Name("wikiTextValidator")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiTextValidator implements Serializable {

    @Logger
    Log log;

    private Map<String, String> validationFailures = new HashMap<String, String>();

    /**
     * Validates the wiki text value and stores the validation state in this component.
     *
     * @param key
     *      The key under which the validation status is stored and can be later retrieved with <tt>isValid()</tt>
     * @param value
     *      The wiki text
     * @param valueRequired
     *      Checks before validation if a value is present and required.
     */
    public void validate(String key, String value, boolean valueRequired) {
        log.debug("validating value of text editor: " + key);
        if (valueRequired && (value == null || value.length() == 0)) {
            log.debug("validation failed for required but null or empty wiki text with key: " + key);
            validationFailures.put(key, "lacewiki.msg.wikiTextValidator.EmptyWikiText");
            return;
        }
        WikiFormattedTextValidator validator = new WikiFormattedTextValidator();
        try {
            validationFailures.remove(key);
            validator.validate(null, null, value);
        } catch (ValidatorException e) {
            log.debug("exception during validation: " + e.getFacesMessage().getSummary());
            validationFailures.put(key, convertFacesMessage(e.getFacesMessage()));
        }
        log.debug("completed validation of text editor value for key: " + key);
    }

    /**
     * Makes it easier to call the validation routine programmatically.
     *
     * @param validationCommand
     *      A command that bundles the validation values and options.
     */
    public void validate(ValidationCommand validationCommand) {
        validate(
            validationCommand.getKey(),
            validationCommand.getWikiTextValue(),
            validationCommand.getWikiTextRequired()
        );
    }

    /**
     * Validation status of wiki text with the given key.
     * <p>
     * <b>Attention: Returns <tt>true</tt> if the given key can not be found!</b>
     * </p>
     *
     * @param key
     *      The key under which the validation status has been stored, i.e. from an earlier <tt>validate()</tt> call
     * @return
     *      True if no status for <tt>key</tt> can be found or if previous validation failed for <tt>key</tt>
     */
    public boolean isValid(String key) {
        return !validationFailures.containsKey(key);
    }

    /**
     * Return the last validation failure message key for the given wiki text key.
     *
     * @param key
     *      The key under which the validation status has been stored, i.e. from an earlier <tt>validate()</tt> call
     * @return
     *      The validation failure message bundle key or null if no error is known.
     */
    public String getValidationFailureMessageBundleKey(String key) {
        return validationFailures.get(key);
    }

    // TODO: These are supposed to be message bundle keys, not the literal ANTLR parser messages, see WikiFormattedTextValidator
    protected String convertFacesMessage(FacesMessage fm) {
        // Convert the FacesMessage to a StatusMessage (which of course is then converted back to JSF...)
        StringBuilder msg = new StringBuilder();
        msg.append(fm.getSummary());

        // Append the detail only if the summary doesn't end with it already
        if (!fm.getSummary().endsWith(fm.getDetail())) {
            msg.append(" (").append(fm.getDetail()).append(")");
        }
        return msg.toString();
    }

    public interface ValidationCommand {
        public String getKey();
        public String getWikiTextValue();
        public boolean getWikiTextRequired();
    }
}
