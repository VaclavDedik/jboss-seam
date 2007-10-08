package org.jboss.seam.ui.validator;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;

import antlr.RecognitionException;
import antlr.TokenStreamException;

/**
 * Formatted Text validator
 * 
 * Use as a JSF validator on an input control that allows entering Seam Text
 * markup.
 * <p>
 * The Seam Text parser has a disabled default error handler, catch exceptions
 * as appropriate if you display Seam Text (see <a
 * href="http://www.doc.ic.ac.uk/lab/secondyear/Antlr/err.html">http://www.doc.ic.ac.uk/lab/secondyear/Antlr/err.html</a>)
 * and call the static convenience method
 * <tt>FormattedTextValidator.getErrorMessage(originalText, recognitionException)</tt>
 * if you want to display or log a nice error message.
 * 
 * @author matthew.drees
 * @author Christian Bauer
 */
public class FormattedTextValidator implements javax.faces.validator.Validator,
        Serializable {
    private static final long serialVersionUID               = 1L;

    private static final int  NUMBER_OF_CONTEXT_CHARS_AFTER  = 10;
    private static final int  NUMBER_OF_CONTEXT_CHARS_BEFORE = 10;

    String                    firstError;

    /**
     * Validate the given value as well-formed Seam Text. If there are parse
     * errors, throw a ValidatorException including the first parse error.
     */
    public void validate(FacesContext context, UIComponent component,
            Object value) throws ValidatorException {
        firstError = null;
        if (value == null) {
            return;
        }

        if (!(value instanceof String)) {
            throw new IllegalArgumentException("Value is not a string: "
                    + value);
        }
        String text = (String) value;
        Reader r = new StringReader(text);
        SeamTextLexer lexer = new SeamTextLexer(r);
        SeamTextParser parser = new SeamTextParser(lexer);
        try {
            parser.startRule();
        }
        // Error handling for ANTLR lexer/parser errors, see
        // http://www.doc.ic.ac.uk/lab/secondyear/Antlr/err.html
        catch (TokenStreamException tse) {
            // Problem with the token input stream
            throw new RuntimeException(tse);
        } catch (RecognitionException re) {
            // A parser error, just log and swallow
            if (firstError == null) {
                firstError = getErrorMessage(text, re);
            }
        }

        if (firstError != null) {
            throw new ValidatorException(new FacesMessage("Invalid markup: "
                    + firstError));
        }
    }

    /**
     * Extracts the error from the <tt>RecognitionException</tt> and generates
     * a message with some helpful context.
     * 
     * @param originalText
     *            the original Seam Text markup as fed into the parser
     * @param re
     *            an ANTLR <tt>RecognitionException</tt> thrown by the parser
     * @return an error message with some helpful context about where the error
     *         occured
     */
    public static String getErrorMessage(String originalText,
            RecognitionException re) {
        int beginIndex = Math.max(re.getColumn() - 1
                - NUMBER_OF_CONTEXT_CHARS_BEFORE, 0);
        int endIndex = Math.min(re.getColumn() + NUMBER_OF_CONTEXT_CHARS_AFTER,
                originalText.length());
        String msg = re.getMessage() + " at '" + (beginIndex == 0 ? "" : "...")
                + originalText.substring(beginIndex, endIndex)
                + (endIndex == originalText.length() ? "" : "...") + "'";
        return msg.replace("\n", " ").replace("\r", " ").replace("\uFFFF","[END OF TEXT]").replace("#{", "# {");
    }
}
