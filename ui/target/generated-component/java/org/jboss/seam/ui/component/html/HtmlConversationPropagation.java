/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UIConversationPropagation ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.ConversationPropagation
 * Component-Family org.jboss.seam.ui.ConversationPropagation
  	 * Customize the conversation propagation for a command link or button (or similar JSF control).
 */
 public class HtmlConversationPropagation extends org.jboss.seam.ui.component.UIConversationPropagation {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationPropagation";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlConversationPropagation (){
  	  }

// Component properties fields
  	/**
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	 	 private String  _pageflow = null; /* Default is null*/
	 	  	/**
	 * type
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	 	 private String  _type = null; /* Default is "none"*/
	 	      
// Getters-setters
     /**
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 * Setter for pageflow
	 * @param pageflow - new value
	 */
	 public void setPageflow( String  __pageflow ){
		this._pageflow = __pageflow;
	 	 }


   /**
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 * Getter for pageflow
	 * @return pageflow value from local variable or value bindings
	 */
	 public String getPageflow(  ){
	         if (null != this._pageflow)
        {
            return this._pageflow;
        	    }
        ValueBinding vb = getValueBinding("pageflow");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Setter for type
	 * @param type - new value
	 */
	 public void setType( String  __type ){
		this._type = __type;
	 	 }


   /**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Getter for type
	 * @return type value from local variable or value bindings
	 */
	 public String getType(  ){
	         if (null != this._type)
        {
            return this._type;
        	    }
        ValueBinding vb = getValueBinding("type");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return "none";
        }
	 	 }
	       
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationPropagation";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
  	 	          values[1] = _pageflow;
	   	 	
  	 	          values[2] = _type;
	   	 	
      	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
  	 	          _pageflow = (String)values[1] ;
	   	 	
  	 	          _type = (String)values[2] ;
	   	 	
      	
		
	}	
// Utilites

}