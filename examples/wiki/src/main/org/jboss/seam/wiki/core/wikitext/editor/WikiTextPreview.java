/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.wikitext.editor;

import org.jboss.seam.annotations.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.log.Log;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

/**
 * Holds conversation-scoped state of wiki text preview feature.
 *
 * @author Christian Bauer
 */
@Name("wikiTextPreview")
@Scope(ScopeType.CONVERSATION)
@AutoCreate
public class WikiTextPreview implements Serializable {

    @Logger
    Log log;

    @In
    WikiTextValidator wikiTextValidator;

    private Map<String, Boolean> previewEnabled = new HashMap<String, Boolean>();

    public void enablePreview(String key) {
        previewEnabled.put(key, true);
    }

    public void enablePreview(String key, String value, boolean valueRequired) {
        // Only enable preview if text passes validation
        wikiTextValidator.validate(key, value, valueRequired);
        if (wikiTextValidator.isValid(key)) previewEnabled.put(key, true);
    }

    public void disablePreview(String key) {
        previewEnabled.remove(key);
    }

    public Boolean isPreviewEnabled(String key) {
        return previewEnabled.get(key);
    }

}
