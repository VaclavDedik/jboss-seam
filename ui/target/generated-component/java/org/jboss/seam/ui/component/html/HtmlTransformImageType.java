/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.graphicImage.UITransformImageType ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.graphicImage.TransformImageType
 * Component-Family org.jboss.seam.ui.graphicImage.TransformImageType
  	 * 
 */
 public class HtmlTransformImageType extends org.jboss.seam.ui.graphicImage.UITransformImageType {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.graphicImage.TransformImageType";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlTransformImageType (){
  	  }

// Component properties fields
    	/**
	 * contentType
	 * 
	 */
	 	 private String  _contentType = null; /* Default is null*/
	 	  
// Getters-setters
       /**
	 * 
	 * Setter for contentType
	 * @param contentType - new value
	 */
	 public void setContentType( String  __contentType ){
		this._contentType = __contentType;
	 	 }


   /**
	 * 
	 * Getter for contentType
	 * @return contentType value from local variable or value bindings
	 */
	 public String getContentType(  ){
	         if (null != this._contentType)
        {
            return this._contentType;
        	    }
        ValueBinding vb = getValueBinding("contentType");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	   
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.graphicImage.TransformImageType";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
    	 	          values[1] = _contentType;
	   	 	
  	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
    	 	          _contentType = (String)values[1] ;
	   	 	
  	
		
	}	
// Utilites

}