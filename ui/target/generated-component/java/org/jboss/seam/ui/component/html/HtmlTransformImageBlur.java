/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.graphicImage.UITransformImageBlur ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.graphicImage.TransformImageBlur
 * Component-Family org.jboss.seam.ui.graphicImage.TransformImageBlur
  	 * 
 */
 public class HtmlTransformImageBlur extends org.jboss.seam.ui.graphicImage.UITransformImageBlur {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.graphicImage.TransformImageBlur";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlTransformImageBlur (){
  	  }

// Component properties fields
 	/**
	 * radius
	 * 
	 */
	 	 private String  _radius = null; /* Default is null*/
	 	     
// Getters-setters
    /**
	 * 
	 * Setter for radius
	 * @param radius - new value
	 */
	 public void setRadius( String  __radius ){
		this._radius = __radius;
	 	 }


   /**
	 * 
	 * Getter for radius
	 * @return radius value from local variable or value bindings
	 */
	 public String getRadius(  ){
	         if (null != this._radius)
        {
            return this._radius;
        	    }
        ValueBinding vb = getValueBinding("radius");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.graphicImage.TransformImageBlur";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
 	 	          values[1] = _radius;
	   	 	
     	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _radius = (String)values[1] ;
	   	 	
     	
		
	}	
// Utilites

}