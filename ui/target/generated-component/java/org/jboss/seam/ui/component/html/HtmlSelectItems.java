/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import java.lang.Boolean ;
import org.jboss.seam.ui.component.UISelectItems ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.SelectItems
 * Component-Family javax.faces.SelectItems
  	 * Creates a List&amp;lt;SelectItem&amp;gt; from a List, Set, DataModel or Array.
 */
 public class HtmlSelectItems extends org.jboss.seam.ui.component.UISelectItems {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.SelectItems";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlSelectItems (){
  	  }

// Component properties fields
 	/**
	 * var
	 * defines the name of the local variable that holds the current object during iteration
	 */
	 	 private String  _var = null; /* Default is null*/
	 	    	/**
	 * disabled
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 */
	 	 private Boolean  _disabled = null; /* Default is null*/
	 	  	/**
	 * hideNoSelectionLabel
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 */
	 	 private Boolean  _hideNoSelectionLabel = null; /* Default is false*/
	 	    	/**
	 * noSelectionLabel
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 */
	 	 private String  _noSelectionLabel = null; /* Default is null*/
	 	   
// Getters-setters
    /**
	 * defines the name of the local variable that holds the current object during iteration
	 * Setter for var
	 * @param var - new value
	 */
	 public void setVar( String  __var ){
		this._var = __var;
	 	 }


   /**
	 * defines the name of the local variable that holds the current object during iteration
	 * Getter for var
	 * @return var value from local variable or value bindings
	 */
	 public String getVar(  ){
	         if (null != this._var)
        {
            return this._var;
        	    }
        ValueBinding vb = getValueBinding("var");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	        /**
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( Boolean  __disabled ){
		this._disabled = __disabled;
	 	 }


   /**
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 * Getter for disabled
	 * @return disabled value from local variable or value bindings
	 */
	 public Boolean getDisabled(  ){
	         if (null != this._disabled)
        {
            return this._disabled;
        	    }
        ValueBinding vb = getValueBinding("disabled");
        if (null != vb){
            return (Boolean)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 * Setter for hideNoSelectionLabel
	 * @param hideNoSelectionLabel - new value
	 */
	 public void setHideNoSelectionLabel( Boolean  __hideNoSelectionLabel ){
		this._hideNoSelectionLabel = __hideNoSelectionLabel;
	 	 }


   /**
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 * Getter for hideNoSelectionLabel
	 * @return hideNoSelectionLabel value from local variable or value bindings
	 */
	 public Boolean getHideNoSelectionLabel(  ){
	         if (null != this._hideNoSelectionLabel)
        {
            return this._hideNoSelectionLabel;
        	    }
        ValueBinding vb = getValueBinding("hideNoSelectionLabel");
        if (null != vb){
            return (Boolean)vb.getValue(getFacesContext());
		        } else {
            return false;
        }
	 	 }
	        /**
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 * Setter for noSelectionLabel
	 * @param noSelectionLabel - new value
	 */
	 public void setNoSelectionLabel( String  __noSelectionLabel ){
		this._noSelectionLabel = __noSelectionLabel;
	 	 }


   /**
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 * Getter for noSelectionLabel
	 * @return noSelectionLabel value from local variable or value bindings
	 */
	 public String getNoSelectionLabel(  ){
	         if (null != this._noSelectionLabel)
        {
            return this._noSelectionLabel;
        	    }
        ValueBinding vb = getValueBinding("noSelectionLabel");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	    
// Component family.
	public static final String COMPONENT_FAMILY = "javax.faces.SelectItems";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[5];
        values[0] = super.saveState(context);
 	 	          values[1] = _var;
	   	 	
    	 	          values[2] = _disabled;
	   	 	
  	 	          values[3] = _hideNoSelectionLabel;
	   	 	
    	 	          values[4] = _noSelectionLabel;
	   	 	
   	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _var = (String)values[1] ;
	   	 	
    	 	          _disabled = (Boolean)values[2] ;
	   	 	
  	 	          _hideNoSelectionLabel = (Boolean)values[3] ;
	   	 	
    	 	          _noSelectionLabel = (String)values[4] ;
	   	 	
   	
		
	}	
// Utilites

}