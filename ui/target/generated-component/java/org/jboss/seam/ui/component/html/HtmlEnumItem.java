/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UIEnumItem ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.EnumItem
 * Component-Family org.jboss.seam.ui.EnumItem
  	 * Creates a SelectItem from an enum value.
 */
 public class HtmlEnumItem extends org.jboss.seam.ui.component.UIEnumItem {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.EnumItem";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlEnumItem (){
  	  }

// Component properties fields
 	/**
	 * enumValue
	 * the string representation of the enum value.
	 */
	 	 private String  _enumValue = null; /* Default is null*/
	 	     	/**
	 * label
	 * the label to be used when rendering the SelectItem.
	 */
	 	 private String  _label = null; /* Default is null*/
	 	        
// Getters-setters
    /**
	 * the string representation of the enum value.
	 * Setter for enumValue
	 * @param enumValue - new value
	 */
	 public void setEnumValue( String  __enumValue ){
		this._enumValue = __enumValue;
	 	 }


   /**
	 * the string representation of the enum value.
	 * Getter for enumValue
	 * @return enumValue value from local variable or value bindings
	 */
	 public String getEnumValue(  ){
	         if (null != this._enumValue)
        {
            return this._enumValue;
        	    }
        ValueBinding vb = getValueBinding("enumValue");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	         /**
	 * the label to be used when rendering the SelectItem.
	 * Setter for label
	 * @param label - new value
	 */
	 public void setLabel( String  __label ){
		this._label = __label;
	 	 }


   /**
	 * the label to be used when rendering the SelectItem.
	 * Getter for label
	 * @return label value from local variable or value bindings
	 */
	 public String getLabel(  ){
	         if (null != this._label)
        {
            return this._label;
        	    }
        ValueBinding vb = getValueBinding("label");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	         
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.EnumItem";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
 	 	          values[1] = _enumValue;
	   	 	
     	 	          values[2] = _label;
	   	 	
        	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _enumValue = (String)values[1] ;
	   	 	
     	 	          _label = (String)values[2] ;
	   	 	
        	
		
	}	
// Utilites

}