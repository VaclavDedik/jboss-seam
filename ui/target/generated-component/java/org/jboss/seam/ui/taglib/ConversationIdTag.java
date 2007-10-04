/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlConversationId;

public class ConversationIdTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
     	/*
	 * name
	 * name
	 */
	 private String  _name = null;

   	/*
	 * viewId
	 * viewId
	 */
	 private String  _viewId = null;

 // Setters
         	/*
	 * name
	 * name
	 */
	/**
	 * name
	 * Setter for name
	 * @param name - new value
	 */
	 public void setName( String  __name ){
		this._name = __name;
     }
	 
     	/*
	 * viewId
	 * viewId
	 */
	/**
	 * viewId
	 * Setter for viewId
	 * @param viewId - new value
	 */
	 public void setViewId( String  __viewId ){
		this._viewId = __viewId;
     }
	 
  // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
     	    this._name = null;
   	    this._viewId = null;
 	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
     		 		 			setStringProperty(component, "name",this._name);
		    		 		 			setStringProperty(component, "viewId",this._viewId);
		     }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.ConversationId";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
