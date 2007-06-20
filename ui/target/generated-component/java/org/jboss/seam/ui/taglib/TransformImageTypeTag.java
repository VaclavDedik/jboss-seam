/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlTransformImageType;

public class TransformImageTypeTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
    	/*
	 * contentType
	 * 
	 */
	 private String  _contentType = null;

  // Setters
       	/*
	 * contentType
	 * 
	 */
	/**
	 * 
	 * Setter for contentType
	 * @param contentType - new value
	 */
	 public void setContentType( String  __contentType ){
		this._contentType = __contentType;
     }
	 
    // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
    	    this._contentType = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
    		 		 			setStringProperty(component, "contentType",this._contentType);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.graphicImage.TransformImageType";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
