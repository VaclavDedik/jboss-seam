/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UILabel ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Label
 * Component-Family javax.faces.Output
  	 * A label associated with the nearest JSF input component
 */
 public class HtmlLabel extends org.jboss.seam.ui.component.UILabel {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Label";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlLabel (){
  	  }

// Component properties fields
                            
// Getters-setters
                            
// Component family.
	public static final String COMPONENT_FAMILY = "javax.faces.Output";

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