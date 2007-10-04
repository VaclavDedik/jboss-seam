/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import java.lang.Object ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlConversationPropagation;

public class ConversationPropagationTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
  	/*
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	 private String  _pageflow = null;

  	/*
	 * type
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	 private String  _type = null;

     	/*
	 * name
	 * A name of this parameter
	 */
	 private String  _name = null;

  // Setters
   	/*
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	/**
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 * Setter for pageflow
	 * @param pageflow - new value
	 */
	 public void setPageflow( String  __pageflow ){
		this._pageflow = __pageflow;
     }
	 
   	/*
	 * type
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	/**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Setter for type
	 * @param type - new value
	 */
	 public void setType( String  __type ){
		this._type = __type;
     }
	 
         	/*
	 * name
	 * A name of this parameter
	 */
	/**
	 * A name of this parameter
	 * Setter for name
	 * @param name - new value
	 */
	 public void setName( String  __name ){
		this._name = __name;
     }
	 
    // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
  	    this._pageflow = null;
  	    this._type = null;
     	    this._name = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
  		 		 			setStringProperty(component, "pageflow",this._pageflow);
		   		 		 			setStringProperty(component, "type",this._type);
		      		 		 			setStringProperty(component, "name",this._name);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.ConversationPropagation";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return null;
			}

}
