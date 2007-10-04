/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlValidateAll;

public class ValidateAllTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
  	/*
	 * validatorsAdded
	 * validatorsAdded
	 */
	 private String  _validatorsAdded = null;

    // Setters
   	/*
	 * validatorsAdded
	 * validatorsAdded
	 */
	/**
	 * validatorsAdded
	 * Setter for validatorsAdded
	 * @param validatorsAdded - new value
	 */
	 public void setValidatorsAdded( String  __validatorsAdded ){
		this._validatorsAdded = __validatorsAdded;
     }
	 
        // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
  	    this._validatorsAdded = null;
    	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
  		 		 			// Simple type - boolean
			setBooleanProperty(component, "validatorsAdded",this._validatorsAdded); 
		        }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.ValidateAll";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.ValidateAllRenderer";
			}

}
