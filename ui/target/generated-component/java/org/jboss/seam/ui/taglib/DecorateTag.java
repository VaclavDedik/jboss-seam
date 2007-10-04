/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlDecorate;

public class DecorateTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * template
	 * 
	 */
	 private String  _template = null;

       	/*
	 * for
	 * 
	 */
	 private String  _for = null;

 // Setters
 	/*
	 * template
	 * 
	 */
	/**
	 * 
	 * Setter for template
	 * @param template - new value
	 */
	 public void setTemplate( String  __template ){
		this._template = __template;
     }
	 
             	/*
	 * for
	 * 
	 */
	/**
	 * 
	 * Setter for for
	 * @param for - new value
	 */
	 public void setFor( String  __for ){
		this._for = __for;
     }
	 
  // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._template = null;
       	    this._for = null;
 	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			setStringProperty(component, "template",this._template);
		        		 		 			setStringProperty(component, "for",this._for);
		     }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.Decorate";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.DecorateRenderer";
			}

}
