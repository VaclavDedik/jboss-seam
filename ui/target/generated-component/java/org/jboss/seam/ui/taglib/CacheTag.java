/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlCache;

public class CacheTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * key
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 */
	 private String  _key = null;

  	/*
	 * enabled
	 * a value expression that determines if the cache should be used.
	 */
	 private String  _enabled = null;

  	/*
	 * region
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 */
	 private String  _region = null;

     // Setters
 	/*
	 * key
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 */
	/**
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 * Setter for key
	 * @param key - new value
	 */
	 public void setKey( String  __key ){
		this._key = __key;
     }
	 
   	/*
	 * enabled
	 * a value expression that determines if the cache should be used.
	 */
	/**
	 * a value expression that determines if the cache should be used.
	 * Setter for enabled
	 * @param enabled - new value
	 */
	 public void setEnabled( String  __enabled ){
		this._enabled = __enabled;
     }
	 
   	/*
	 * region
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 */
	/**
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 * Setter for region
	 * @param region - new value
	 */
	 public void setRegion( String  __region ){
		this._region = __region;
     }
	 
          // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._key = null;
  	    this._enabled = null;
  	    this._region = null;
     	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			setStringProperty(component, "key",this._key);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "enabled",this._enabled); 
		   		 		 			setStringProperty(component, "region",this._region);
		         }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.Cache";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.CacheRenderer";
			}

}
