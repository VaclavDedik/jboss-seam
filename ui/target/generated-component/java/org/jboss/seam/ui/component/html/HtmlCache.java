/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UICache ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Cache
 * Component-Family org.jboss.seam.ui.Cache
  	 * Renderer-Type org.jboss.seam.ui.CacheRenderer
  	 * Cache the rendered page fragment using JBoss Cache. Note that &amp;lt;s:cache&amp;gt; actually uses the instance of JBoss Cache managed by the built-in pojoCache component.
 */
 public class HtmlCache extends org.jboss.seam.ui.component.UICache {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Cache";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlCache (){
  	  	setRendererType("org.jboss.seam.ui.CacheRenderer");
  	  }

// Component properties fields
 	/**
	 * key
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 */
	 	 private String  _key = null; /* Default is null*/
	 	  	/**
	 * enabled
	 * a value expression that determines if the cache should be used.
	 */
	 	 private boolean  _enabled = true;		
	/**
	 * Flag indicated what enabled is set.
	 */
	 private boolean _enabledSet = false;	
	 	  	/**
	 * region
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 */
	 	 private String  _region = null; /* Default is null*/
	 	     
// Getters-setters
    /**
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 * Setter for key
	 * @param key - new value
	 */
	 public void setKey( String  __key ){
		this._key = __key;
	 	 }


   /**
	 * the key to cache rendered content, often a value expression. For example, if we were caching a page fragment that displays a document, we might use key="Document-#{document.id}".
	 * Getter for key
	 * @return key value from local variable or value bindings
	 */
	 public String getKey(  ){
	         if (null != this._key)
        {
            return this._key;
        	    }
        ValueBinding vb = getValueBinding("key");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * a value expression that determines if the cache should be used.
	 * Setter for enabled
	 * @param enabled - new value
	 */
	 public void setEnabled( boolean  __enabled ){
		this._enabled = __enabled;
	 		this._enabledSet = true;
	 	 }


   /**
	 * a value expression that determines if the cache should be used.
	 * Getter for enabled
	 * @return enabled value from local variable or value bindings
	 */
	 public boolean isEnabled(  ){
	 		 if(this._enabledSet){
			return this._enabled;
		 }
    	ValueBinding vb = getValueBinding("enabled");
    	if (vb != null) {
    	    Boolean value = (Boolean) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._enabled;
    	    }
    	    return (value.booleanValue());
    	} else {
    	    return (this._enabled);
    	}
	 	 }
	      /**
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 * Setter for region
	 * @param region - new value
	 */
	 public void setRegion( String  __region ){
		this._region = __region;
	 	 }


   /**
	 * a JBoss Cache node to use (different nodes can have different expiry policies).
	 * Getter for region
	 * @return region value from local variable or value bindings
	 */
	 public String getRegion(  ){
	         if (null != this._region)
        {
            return this._region;
        	    }
        ValueBinding vb = getValueBinding("region");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Cache";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[5];
        values[0] = super.saveState(context);
 	 	          values[1] = _key;
	   	 	
  	        values[2] = new Boolean(_enabled);
	   	   values[3] = Boolean.valueOf(_enabledSet);	
	   	 	
  	 	          values[4] = _region;
	   	 	
     	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _key = (String)values[1] ;
	   	 	
  	        _enabled = ((Boolean)values[2]).booleanValue();
	   	   _enabledSet = ((Boolean)values[3]).booleanValue();	
	   	 	
  	 	          _region = (String)values[4] ;
	   	 	
     	
		
	}	
// Utilites

}