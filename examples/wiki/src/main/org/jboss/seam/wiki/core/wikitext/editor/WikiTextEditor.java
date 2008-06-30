/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.editor;

import org.jboss.seam.log.Log;
import org.jboss.seam.log.Logging;
import org.jboss.seam.wiki.core.wikitext.engine.WikiLinkResolver;
import org.jboss.seam.wiki.core.model.WikiFile;
import org.jboss.seam.wiki.core.action.Validatable;
import org.jboss.seam.Component;

import javax.faces.validator.ValidatorException;
import javax.faces.application.FacesMessage;
import java.util.HashSet;
import java.util.Set;
import java.io.Serializable;

/**
 * A wiki (or plain) text editor.
 *
 * @author Christian Bauer
 */
public class WikiTextEditor implements Validatable, Serializable {

    Log log = Logging.getLog(WikiTextEditor.class);

    // Construction time
    private String key;
    private int valueMaxLength = 32767;
    private boolean valueRequired = true;
    private boolean allowPlaintext = false;
    private int rows = 20;

    // Editing
    private String value;
    private boolean valid = true;
    private boolean valuePlaintext;
    private boolean previewEnabled;
    private String lastValidationError;
    private Set<WikiFile> linkTargets;

    public WikiTextEditor(String key) {
        this.key = key;
    }

    public WikiTextEditor(String key, int valueMaxLength) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired, boolean allowPlaintext) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
        this.allowPlaintext = allowPlaintext;
    }

    public WikiTextEditor(String key, int valueMaxLength, boolean valueRequired, boolean allowPlaintext, int rows) {
        this.key = key;
        this.valueMaxLength = valueMaxLength;
        this.valueRequired = valueRequired;
        this.allowPlaintext = allowPlaintext;
        this.rows = rows;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        // Stupid Internet Explorer textarea puts carriage returns inside the text, we don't want any of that
        this.value = value != null ? value.replaceAll("\r", "") : value;
    }

    public int getValueMaxLength() {
        return valueMaxLength;
    }

    public void setValueMaxLength(int valueMaxLength) {
        this.valueMaxLength = valueMaxLength;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }

    public void setValueRequired(boolean valueRequired) {
        this.valueRequired = valueRequired;
    }

    public boolean isAllowPlaintext() {
        return allowPlaintext;
    }

    public void setAllowPlaintext(boolean allowPlaintext) {
        this.allowPlaintext = allowPlaintext;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isValuePlaintext() {
        return valuePlaintext;
    }

    public void setValuePlaintext(boolean valuePlaintext) {
        this.valuePlaintext = valuePlaintext;
    }

    public boolean isPreviewEnabled() {
        return previewEnabled;
    }

    public void setPreviewEnabled(boolean previewEnabled) {
        if (previewEnabled) {
            validate();
        } else {
            setValid(true);
        }
        this.previewEnabled = previewEnabled;
    }

    public String getLastValidationError() {
        return lastValidationError;
    }

    public void setLastValidationError(String lastValidationError) {
        this.lastValidationError = lastValidationError;
    }

    public Set<WikiFile> getLinkTargets() {
        return linkTargets;
    }

    public void setValueAndConvertLinks(Long areaNumber, String value) {
        log.debug("setting value and resolving wiki://links to clear text");
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver) Component.getInstance("wikiLinkResolver");
        setValue(wikiLinkResolver.convertFromWikiProtocol(areaNumber, value));
    }

    public String getValueAndConvertLinks(Long areaNumber) {
        log.debug("setting value and resolving clear text to wiki://links");
        WikiLinkResolver wikiLinkResolver = (WikiLinkResolver)Component.getInstance("wikiLinkResolver");
        linkTargets = new HashSet<WikiFile>();
        return wikiLinkResolver.convertToWikiProtocol(linkTargets, areaNumber, getValue());
    }

    public int getRemainingCharacters() {
        return getValue() != null ? getValueMaxLength() - getValue().length() : getValueMaxLength();
    }

    public void switchPlaintext() {
        // If the user wants to switch from plain text back to wiki text, do validation
        if (!isValuePlaintext()) {
            validate();
            // Allow only if valid wiki text markup
            setValuePlaintext(!isValid());
        } else {
            // If the user wants plain text, then we can discard any validation errors
            setValid(true);
            lastValidationError = null;
        }
    }

    public void validate() {
        log.debug("validating value of text editor: " + key);
        setValid(false);
        if (valueRequired && (value == null || value.length() == 0)) {
            log.debug("validation failed for required but null or empty wiki text with key: " + key);
            lastValidationError = "lacewiki.msg.wikiTextValidator.EmptyWikiText"; // TODO: make static
            return;
        }
        if (value != null && value.length() > getValueMaxLength()) {
            log.debug("validation failed for too long wiki text with key: " + key);
            lastValidationError = "lacewiki.msg.wikiTextValidator.MaxLengthExceeded"; // TODO: make static
            return;
        }
        try {
            lastValidationError = null;
            if (!isValuePlaintext()) {
                WikiFormattedTextValidator validator = new WikiFormattedTextValidator();
                validator.validate(null, null, value);
            }
            log.debug("value is valid");
            setValid(true);
        } catch (ValidatorException e) {
            log.debug("exception during validation: " + e.getFacesMessage().getSummary());
            lastValidationError = convertFacesMessage(e.getFacesMessage());
        }
        log.debug("completed validation of text editor value for key: " + key);
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

    /* TODO: Old stuff
    public void setShowPluginPrefs(boolean showPluginPrefs) {
        Contexts.getPageContext().set("showPluginPreferences", showPluginPrefs);
    }

    public boolean isShowPluginPrefs() {
        Boolean showPluginPrefs = (Boolean)Contexts.getPageContext().get("showPluginPreferences");
        return showPluginPrefs != null && showPluginPrefs;
    }
    */
}
