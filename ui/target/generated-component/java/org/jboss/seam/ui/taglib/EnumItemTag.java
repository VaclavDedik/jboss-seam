/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlEnumItem;

public class EnumItemTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * enumValue
	 * the string representation of the enum value.
	 */
	 private String  _enumValue = null;

   	/*
	 * itemEscaped
	 * itemEscaped
	 */
	 private String  _itemEscaped = null;

  	/*
	 * itemDescription
	 * A description used by tools only
	 */
	 private String  _itemDescription = null;

  	/*
	 * label
	 * the label to be used when rendering the SelectItem.
	 */
	 private String  _label = null;

  	/*
	 * itemLabel
	 * A text shown by the item
	 */
	 private String  _itemLabel = null;

  	/*
	 * itemDisabled
	 * If "true", this component isn't saved during state saving
	 */
	 private String  _itemDisabled = null;

     	/*
	 * itemValue
	 * Item value, which is passed to the server as a request parameter
	 */
	 private String  _itemValue = null;

  // Setters
 	/*
	 * enumValue
	 * the string representation of the enum value.
	 */
	/**
	 * the string representation of the enum value.
	 * Setter for enumValue
	 * @param enumValue - new value
	 */
	 public void setEnumValue( String  __enumValue ){
		this._enumValue = __enumValue;
     }
	 
     	/*
	 * itemEscaped
	 * itemEscaped
	 */
	/**
	 * itemEscaped
	 * Setter for itemEscaped
	 * @param itemEscaped - new value
	 */
	 public void setItemEscaped( String  __itemEscaped ){
		this._itemEscaped = __itemEscaped;
     }
	 
   	/*
	 * itemDescription
	 * A description used by tools only
	 */
	/**
	 * A description used by tools only
	 * Setter for itemDescription
	 * @param itemDescription - new value
	 */
	 public void setItemDescription( String  __itemDescription ){
		this._itemDescription = __itemDescription;
     }
	 
   	/*
	 * label
	 * the label to be used when rendering the SelectItem.
	 */
	/**
	 * the label to be used when rendering the SelectItem.
	 * Setter for label
	 * @param label - new value
	 */
	 public void setLabel( String  __label ){
		this._label = __label;
     }
	 
   	/*
	 * itemLabel
	 * A text shown by the item
	 */
	/**
	 * A text shown by the item
	 * Setter for itemLabel
	 * @param itemLabel - new value
	 */
	 public void setItemLabel( String  __itemLabel ){
		this._itemLabel = __itemLabel;
     }
	 
   	/*
	 * itemDisabled
	 * If "true", this component isn't saved during state saving
	 */
	/**
	 * If "true", this component isn't saved during state saving
	 * Setter for itemDisabled
	 * @param itemDisabled - new value
	 */
	 public void setItemDisabled( String  __itemDisabled ){
		this._itemDisabled = __itemDisabled;
     }
	 
         	/*
	 * itemValue
	 * Item value, which is passed to the server as a request parameter
	 */
	/**
	 * Item value, which is passed to the server as a request parameter
	 * Setter for itemValue
	 * @param itemValue - new value
	 */
	 public void setItemValue( String  __itemValue ){
		this._itemValue = __itemValue;
     }
	 
    // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._enumValue = null;
   	    this._itemEscaped = null;
  	    this._itemDescription = null;
  	    this._label = null;
  	    this._itemLabel = null;
  	    this._itemDisabled = null;
     	    this._itemValue = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			setStringProperty(component, "enumValue",this._enumValue);
		    		 		 			// Simple type - boolean
			setBooleanProperty(component, "itemEscaped",this._itemEscaped); 
		   		 		 			setStringProperty(component, "itemDescription",this._itemDescription);
		   		 		 			setStringProperty(component, "label",this._label);
		   		 		 			setStringProperty(component, "itemLabel",this._itemLabel);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "itemDisabled",this._itemDisabled); 
		      		 		 			// TODO - handle object
			setStringProperty(component, "itemValue",this._itemValue);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.EnumItem";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
