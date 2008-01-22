/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.LoadStyle
 * Component-Family org.ajax4jsf.LoadStyle
  	 * Add a stylesheet to the &amp;lt;head&amp;gt; of the page.  Any EL in the CSS will be resolved.
 */
 public class HtmlLoadStyle extends org.jboss.seam.ui.component.UILoadStyle {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.LoadStyle";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlLoadStyle (){
  	  }

// Component properties fields
 	/**
	 * isolated
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 */
	 	 private boolean  _isolated = false;		
	/**
	 * Flag indicated what isolated is set.
	 */
	 private boolean _isolatedSet = false;	
	 	     
// Getters-setters
    /**
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 * Setter for isolated
	 * @param __isolated - new value
	 */
	 public void setIsolated( boolean  __isolated ){
		this._isolated = __isolated;
	 		this._isolatedSet = true;
	 	 }


   /**
	 * If isolated, any references to html ids will be resolved only within
			this naming container
	 * Getter for isolated
	 * @return isolated value from local variable or value bindings
	 */
	 public boolean isIsolated(  ){
	 		 if(this._isolatedSet){
			return this._isolated;
		 }
    	ValueBinding vb = getValueBinding("isolated");
    	if (vb != null) {
    	    Boolean value = (Boolean) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._isolated;
    	    }
    	    return (value.booleanValue());
    	} else {
    	    return (this._isolated);
    	}
	 	 }
	      
// Component family.
	public static final String COMPONENT_FAMILY = "org.ajax4jsf.LoadStyle";

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
 	        values[1] = new Boolean(_isolated);
	   	   values[2] = Boolean.valueOf(_isolatedSet);	
	   	 	
     	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	        _isolated = ((Boolean)values[1]).booleanValue();
	   	   _isolatedSet = ((Boolean)values[2]).booleanValue();	
	   	 	
     	
		
	}	
// Utilites

}