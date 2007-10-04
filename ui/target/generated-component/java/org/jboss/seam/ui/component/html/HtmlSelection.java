/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UISelection ;
import java.lang.String ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Selection
 * Component-Family org.jboss.seam.ui.Selection
  	 * 
 */
 public class HtmlSelection extends org.jboss.seam.ui.component.UISelection {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Selection";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlSelection (){
  	  }

// Component properties fields
 	/**
	 * var
	 * var
	 */
	 	 private String  _var = null; /* Default is null*/
	 	   	/**
	 * dataModel
	 * dataModel
	 */
	 	 private String  _dataModel = null; /* Default is null*/
	 	      
// Getters-setters
    /**
	 * var
	 * Setter for var
	 * @param var - new value
	 */
	 public void setVar( String  __var ){
		this._var = __var;
	 	 }


   /**
	 * var
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
	 * dataModel
	 * Setter for dataModel
	 * @param dataModel - new value
	 */
	 public void setDataModel( String  __dataModel ){
		this._dataModel = __dataModel;
	 	 }


   /**
	 * dataModel
	 * Getter for dataModel
	 * @return dataModel value from local variable or value bindings
	 */
	 public String getDataModel(  ){
	         if (null != this._dataModel)
        {
            return this._dataModel;
        	    }
        ValueBinding vb = getValueBinding("dataModel");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Selection";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
 	 	          values[1] = _var;
	   	 	
   	 	          values[2] = _dataModel;
	   	 	
      	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _var = (String)values[1] ;
	   	 	
   	 	          _dataModel = (String)values[2] ;
	   	 	
      	
		
	}	
// Utilites

}