/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import java.lang.Boolean ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlSelectItems;

public class SelectItemsTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * var
	 * defines the name of the local variable that holds the current object during iteration
	 */
	 private String  _var = null;

   	/*
	 * label
	 * the label to be used when rendering the SelectItem. Can reference the var variable
	 */
	 private String  _label = null;

  	/*
	 * disabled
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 */
	 private String  _disabled = null;

  	/*
	 * hideNoSelectionLabel
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 */
	 private String  _hideNoSelectionLabel = null;

    	/*
	 * noSelectionLabel
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 */
	 private String  _noSelectionLabel = null;

   // Setters
 	/*
	 * var
	 * defines the name of the local variable that holds the current object during iteration
	 */
	/**
	 * defines the name of the local variable that holds the current object during iteration
	 * Setter for var
	 * @param var - new value
	 */
	 public void setVar( String  __var ){
		this._var = __var;
     }
	 
     	/*
	 * label
	 * the label to be used when rendering the SelectItem. Can reference the var variable
	 */
	/**
	 * the label to be used when rendering the SelectItem. Can reference the var variable
	 * Setter for label
	 * @param label - new value
	 */
	 public void setLabel( String  __label ){
		this._label = __label;
     }
	 
   	/*
	 * disabled
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 */
	/**
	 * if true the SelectItem will be rendered disabled. Can reference the var variable
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( String  __disabled ){
		this._disabled = __disabled;
     }
	 
   	/*
	 * hideNoSelectionLabel
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 */
	/**
	 * if true, the noSelectionLabel will be hidden when a value is selected
	 * Setter for hideNoSelectionLabel
	 * @param hideNoSelectionLabel - new value
	 */
	 public void setHideNoSelectionLabel( String  __hideNoSelectionLabel ){
		this._hideNoSelectionLabel = __hideNoSelectionLabel;
     }
	 
       	/*
	 * noSelectionLabel
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 */
	/**
	 * specifies the (optional) label to place at the top of list (if required="true" is also specified then selecting this value will cause a validation error)
	 * Setter for noSelectionLabel
	 * @param noSelectionLabel - new value
	 */
	 public void setNoSelectionLabel( String  __noSelectionLabel ){
		this._noSelectionLabel = __noSelectionLabel;
     }
	 
      // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._var = null;
   	    this._label = null;
  	    this._disabled = null;
  	    this._hideNoSelectionLabel = null;
    	    this._noSelectionLabel = null;
   	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			setStringProperty(component, "var",this._var);
		    		 		 			// TODO - handle object
			setStringProperty(component, "label",this._label);
		   		 		 			setBooleanProperty(component, "disabled",this._disabled); 
		   		 		 			setBooleanProperty(component, "hideNoSelectionLabel",this._hideNoSelectionLabel); 
		     		 		 			setStringProperty(component, "noSelectionLabel",this._noSelectionLabel);
		       }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.SelectItems";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
