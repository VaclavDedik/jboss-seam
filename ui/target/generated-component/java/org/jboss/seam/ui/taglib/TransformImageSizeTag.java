/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Double ;
import java.lang.Integer ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlTransformImageSize;

public class TransformImageSizeTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * factor
	 * 
	 */
	 private String  _factor = null;

  	/*
	 * width
	 * 
	 */
	 private String  _width = null;

  	/*
	 * height
	 * 
	 */
	 private String  _height = null;

      	/*
	 * maintainRatio
	 * 
	 */
	 private String  _maintainRatio = null;

 // Setters
 	/*
	 * factor
	 * 
	 */
	/**
	 * 
	 * Setter for factor
	 * @param factor - new value
	 */
	 public void setFactor( String  __factor ){
		this._factor = __factor;
     }
	 
   	/*
	 * width
	 * 
	 */
	/**
	 * 
	 * Setter for width
	 * @param width - new value
	 */
	 public void setWidth( String  __width ){
		this._width = __width;
     }
	 
   	/*
	 * height
	 * 
	 */
	/**
	 * 
	 * Setter for height
	 * @param height - new value
	 */
	 public void setHeight( String  __height ){
		this._height = __height;
     }
	 
           	/*
	 * maintainRatio
	 * 
	 */
	/**
	 * 
	 * Setter for maintainRatio
	 * @param maintainRatio - new value
	 */
	 public void setMaintainRatio( String  __maintainRatio ){
		this._maintainRatio = __maintainRatio;
     }
	 
  // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._factor = null;
  	    this._width = null;
  	    this._height = null;
      	    this._maintainRatio = null;
 	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			// TODO - setup properties for other cases.
			// name factor with type java.lang.Double
		   		 		 			setIntegerProperty(component, "width",this._width); 
		   		 		 			setIntegerProperty(component, "height",this._height); 
		       		 		 			// Simple type - boolean
			setBooleanProperty(component, "maintainRatio",this._maintainRatio); 
		     }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.graphicImage.TransformImageSize";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
