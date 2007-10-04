/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UIFormattedText ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.FormattedText
 * Component-Family org.jboss.seam.ui.FormattedText
  	 * Renderer-Type org.jboss.seam.ui.FormattedTextRenderer
  	 * Output Seam Text. Parse errors generate WARN level log messages.
 */
 public class HtmlFormattedText extends org.jboss.seam.ui.component.UIFormattedText {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.FormattedText";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlFormattedText (){
  	  	setRendererType("org.jboss.seam.ui.FormattedTextRenderer");
  	  }

// Component properties fields
        
// Getters-setters
        
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FormattedText";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[1];
        values[0] = super.saveState(context);
        	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
        	
		
	}	
// Utilites

}