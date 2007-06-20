/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlTaskId;

public class TaskIdTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
     	/*
	 * name
	 * name
	 */
	 private String  _name = null;

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
	 
    // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
     	    this._name = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
     		 		 			setStringProperty(component, "name",this._name);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.TaskId";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
