/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UIDecorate ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Decorate
 * Component-Family org.jboss.seam.ui.Decorate
  	 * Renderer-Type org.jboss.seam.ui.DecorateRenderer
  	 * "Decorate" a JSF input field when validation fails or when required="true" is set.
 */
 public class HtmlDecorate extends org.jboss.seam.ui.component.UIDecorate {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Decorate";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlDecorate (){
  	  	setRendererType("org.jboss.seam.ui.DecorateRenderer");
  	  }

// Component properties fields
      	/**
	 * for
	 * 
	 */
	 	 private String  _for = null; /* Default is null*/
	 	 
// Getters-setters
         /**
	 * 
	 * Setter for for
	 * @param for - new value
	 */
	 public void setFor( String  __for ){
		this._for = __for;
	 	 }


   /**
	 * 
	 * Getter for for
	 * @return for value from local variable or value bindings
	 */
	 public String getFor(  ){
	         if (null != this._for)
        {
            return this._for;
        	    }
        ValueBinding vb = getValueBinding("for");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	  
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorate";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
      	 	          values[1] = saveAttachedState(context, _for );		
	   	 	
 	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
      	 	          _for = (String)restoreAttachedState(context,values[1] );		
	   	 	
 	
		
	}	
// Utilites

}