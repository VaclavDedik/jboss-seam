/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UIRemote ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Remote
 * Component-Family org.jboss.seam.ui.Remote
  	 * Renderer-Type org.jboss.seam.ui.RemoteRenderer
  	 * 
 */
 public class HtmlRemote extends org.jboss.seam.ui.component.UIRemote {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Remote";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlRemote (){
  	  	setRendererType("org.jboss.seam.ui.RemoteRenderer");
  	  }

// Component properties fields
 	/**
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 	 private String  _styleClass = null; /* Default is null*/
	 	  	/**
	 * include
	 * 
	 */
	 	 private String  _include = null; /* Default is null*/
	 	  	/**
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 	 private String  _style = null; /* Default is null*/
	 	     
// Getters-setters
    /**
	 * Corresponds to the HTML class attribute
	 * Setter for styleClass
	 * @param styleClass - new value
	 */
	 public void setStyleClass( String  __styleClass ){
		this._styleClass = __styleClass;
	 	 }


   /**
	 * Corresponds to the HTML class attribute
	 * Getter for styleClass
	 * @return styleClass value from local variable or value bindings
	 */
	 public String getStyleClass(  ){
	         if (null != this._styleClass)
        {
            return this._styleClass;
        	    }
        ValueBinding vb = getValueBinding("styleClass");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * 
	 * Setter for include
	 * @param include - new value
	 */
	 public void setInclude( String  __include ){
		this._include = __include;
	 	 }


   /**
	 * 
	 * Getter for include
	 * @return include value from local variable or value bindings
	 */
	 public String getInclude(  ){
	         if (null != this._include)
        {
            return this._include;
        	    }
        ValueBinding vb = getValueBinding("include");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * CSS style(s) is/are to be applied when this component is rendered
	 * Setter for style
	 * @param style - new value
	 */
	 public void setStyle( String  __style ){
		this._style = __style;
	 	 }


   /**
	 * CSS style(s) is/are to be applied when this component is rendered
	 * Getter for style
	 * @return style value from local variable or value bindings
	 */
	 public String getStyle(  ){
	         if (null != this._style)
        {
            return this._style;
        	    }
        ValueBinding vb = getValueBinding("style");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Remote";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[4];
        values[0] = super.saveState(context);
 	 	          values[1] = _styleClass;
	   	 	
  	 	          values[2] = _include;
	   	 	
  	 	          values[3] = _style;
	   	 	
     	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _styleClass = (String)values[1] ;
	   	 	
  	 	          _include = (String)values[2] ;
	   	 	
  	 	          _style = (String)values[3] ;
	   	 	
     	
		
	}	
// Utilites

}