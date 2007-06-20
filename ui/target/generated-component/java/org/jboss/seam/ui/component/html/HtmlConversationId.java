/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UIConversationId ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.ConversationId
 * Component-Family org.jboss.seam.ui.ConversationId
  	 * Add the conversation id to an output link (or similar JSF control).
 */
 public class HtmlConversationId extends org.jboss.seam.ui.component.UIConversationId {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.ConversationId";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlConversationId (){
  	  }

// Component properties fields
       	/**
	 * viewId
	 * viewId
	 */
	 	 private String  _viewId = null; /* Default is null*/
	 	 
// Getters-setters
          /**
	 * viewId
	 * Setter for viewId
	 * @param viewId - new value
	 */
	 public void setViewId( String  __viewId ){
		this._viewId = __viewId;
	 	 }


   /**
	 * viewId
	 * Getter for viewId
	 * @return viewId value from local variable or value bindings
	 */
	 public String getViewId(  ){
	         if (null != this._viewId)
        {
            return this._viewId;
        	    }
        ValueBinding vb = getValueBinding("viewId");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	  
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.ConversationId";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[2];
        values[0] = super.saveState(context);
       	 	          values[1] = _viewId;
	   	 	
 	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
       	 	          _viewId = (String)values[1] ;
	   	 	
 	
		
	}	
// Utilites

}