package org.jboss.seam.wiki.core.ui;

import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.faces.Validator;
import org.jboss.seam.text.SeamTextLexer;
import org.jboss.seam.text.SeamTextParser;

import antlr.ANTLRException;
import antlr.RecognitionException;

/**
 * Seam Text validator
 * @author matthew.drees
 *
 */
@Validator
@Name("seamTextValidator")
public class SeamTextValidator implements javax.faces.validator.Validator, Serializable {
	private static final long serialVersionUID = 1L;

	String firstError;

	/**
	 * Validate the given value as well-formed Seam Text.  If there are parse errors, throw a ValidatorException
	 * including the first parse error.
	 */
	public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
		firstError = null;
		if (value == null) {
			return;
		}

		if (!(value instanceof String)){
			throw new IllegalArgumentException("Value is not a string: " + value);
		}
		String text = (String) value;
        Reader r = new StringReader(text);
        SeamTextLexer lexer = new SeamTextLexer(r);
        SeamTextParser parser = new SeamTextParser(lexer) {

			@Override
			public void reportError(RecognitionException re) {
				if (firstError == null) {
					firstError = re.getMessage();
				}
			}
        };
        try {
			parser.startRule();
		}
        catch (ANTLRException re) {
			throw new RuntimeException("Can't parse text", re);
		}
        if (firstError != null) {
        	firstError = firstError.replace("\uFFFF", "[END OF TEXT]");
        	throw new ValidatorException(new FacesMessage("Invalid text: " + firstError));
        }
	}
}
