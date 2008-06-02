/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.wiki.core.engine;

import org.jboss.seam.ui.validator.FormattedTextValidator;
import org.jboss.seam.text.SeamTextParser;
import antlr.SemanticException;

/**
 * Disables the Seam Text validation for link tags, so wiki links are OK.
 *
 * @author Christian Bauer
 */
public class WikiFormattedTextValidator extends FormattedTextValidator {

    public SeamTextParser getSeamTextParser(String s) {
        SeamTextParser parser = super.getSeamTextParser(s);
        parser.setSanitizer(
            new SeamTextParser.DefaultSanitizer() {
                @Override
                public void validateLinkTagURI(String s) throws SemanticException {}
            }
        );
        return parser;
    }

}
