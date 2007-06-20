/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UIValidateAll ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.ValidateAll
 * Component-Family org.jboss.seam.ui.ValidateAll
  	 * Renderer-Type org.jboss.seam.ui.ValidateAllRenderer
  	 * Validate all child JSF input fields against the bound propertys using Hibernate Validator.
 */
 public class HtmlValidateAll extends org.jboss.seam.ui.component.UIValidateAll {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.ValidateAll";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlValidateAll (){
  	  	setRendererType("org.jboss.seam.ui.ValidateAllRenderer");
  	  }

// Component properties fields
     
// Getters-setters
     
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ValidateAll";

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