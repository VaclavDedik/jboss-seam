/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UIFragment ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Fragment
 * Component-Family org.jboss.seam.ui.Fragment
  	 * Renderer-Type org.jboss.seam.ui.FragmentRenderer
  	 * 
 */
 public class HtmlFragment extends org.jboss.seam.ui.component.UIFragment {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Fragment";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlFragment (){
  	  	setRendererType("org.jboss.seam.ui.FragmentRenderer");
  	  }

// Component properties fields
    
// Getters-setters
    
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Fragment";

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