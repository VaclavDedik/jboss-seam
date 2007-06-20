/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlLoadStyle;

public class LoadStyleTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * isolated
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 */
	 private String  _isolated = null;

  	/*
	 * src
	 * src
	 */
	 private String  _src = null;

     // Setters
 	/*
	 * isolated
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 */
	/**
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 * Setter for isolated
	 * @param isolated - new value
	 */
	 public void setIsolated( String  __isolated ){
		this._isolated = __isolated;
     }
	 
   	/*
	 * src
	 * src
	 */
	/**
	 * src
	 * Setter for src
	 * @param src - new value
	 */
	 public void setSrc( String  __src ){
		this._src = __src;
     }
	 
          // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._isolated = null;
  	    this._src = null;
     	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			// Simple type - boolean
			setBooleanProperty(component, "isolated",this._isolated); 
		   		 		 			// TODO - handle object
			setStringProperty(component, "src",this._src);
		         }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.LoadStyle";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
