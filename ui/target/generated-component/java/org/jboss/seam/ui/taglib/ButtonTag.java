/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.convert.Converter ;
import java.lang.Object ;
import javax.faces.el.MethodBinding ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlButton;

public class ButtonTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * actionListener
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 */
	 private String  _actionListener = null;

    	/*
	 * title
	 * Advisory title information about markup elements generated for this component
	 */
	 private String  _title = null;

     	/*
	 * dir
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 */
	 private String  _dir = null;

  	/*
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 private String  _styleClass = null;

  	/*
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	 private String  _pageflow = null;

  	/*
	 * outcome
	 * outcome
	 */
	 private String  _outcome = null;

  	/*
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	 private String  _accesskey = null;

  	/*
	 * fragment
	 * the fragment identifier to link to.
	 */
	 private String  _fragment = null;

  	/*
	 * onkeypress
	 * HTML: a script expression; a key is pressed and released
	 */
	 private String  _onkeypress = null;

  	/*
	 * ondblclick
	 * HTML: a script expression; a pointer button is double-clicked
	 */
	 private String  _ondblclick = null;

  	/*
	 * image
	 * image
	 */
	 private String  _image = null;

  	/*
	 * propagation
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	 private String  _propagation = null;

  	/*
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 private String  _style = null;

   	/*
	 * size
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 */
	 private String  _size = null;

  	/*
	 * onmouseover
	 * HTML: a script expression; a pointer is moved onto
	 */
	 private String  _onmouseover = null;

  	/*
	 * view
	 * the JSF view id to link to.
	 */
	 private String  _view = null;

  	/*
	 * action
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 */
	 private String  _action = null;

   	/*
	 * onkeyup
	 * HTML: a script expression; a key is released
	 */
	 private String  _onkeyup = null;

  	/*
	 * tabindex
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 */
	 private String  _tabindex = null;

   	/*
	 * type
	 * submit|reset|image|button This attribute specifies a type of control to create. The default value for this attribute is "submit"
	 */
	 private String  _type = null;

  	/*
	 * lang
	 * Code describing the language used in the generated markup for this component
	 */
	 private String  _lang = null;

  	/*
	 * disabled
	 * When set for a form control, this boolean attribute disables the control for user input.
	 */
	 private String  _disabled = null;

  	/*
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	 private String  _onclick = null;

   	/*
	 * alt
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 */
	 private String  _alt = null;

  	/*
	 * onmouseout
	 * HTML: a script expression; a pointer is moved away
	 */
	 private String  _onmouseout = null;

  	/*
	 * onkeydown
	 * HTML: a script expression; a key is pressed down
	 */
	 private String  _onkeydown = null;

  	/*
	 * onmousedown
	 * HTML: script expression; a pointer button is pressed down
	 */
	 private String  _onmousedown = null;

  	/*
	 * immediate
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 */
	 private String  _immediate = null;

  	/*
	 * onmouseup
	 * HTML: script expression; a pointer button is released
	 */
	 private String  _onmouseup = null;

    	/*
	 * onmousemove
	 * HTML: a script expression; a pointer is moved within
	 */
	 private String  _onmousemove = null;

 // Setters
 	/*
	 * actionListener
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 */
	/**
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 * Setter for actionListener
	 * @param actionListener - new value
	 */
	 public void setActionListener( String  __actionListener ){
		this._actionListener = __actionListener;
     }
	 
       	/*
	 * title
	 * Advisory title information about markup elements generated for this component
	 */
	/**
	 * Advisory title information about markup elements generated for this component
	 * Setter for title
	 * @param title - new value
	 */
	 public void setTitle( String  __title ){
		this._title = __title;
     }
	 
         	/*
	 * dir
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 */
	/**
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 * Setter for dir
	 * @param dir - new value
	 */
	 public void setDir( String  __dir ){
		this._dir = __dir;
     }
	 
   	/*
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	/**
	 * Corresponds to the HTML class attribute
	 * Setter for styleClass
	 * @param styleClass - new value
	 */
	 public void setStyleClass( String  __styleClass ){
		this._styleClass = __styleClass;
     }
	 
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
	 * outcome
	 * outcome
	 */
	/**
	 * outcome
	 * Setter for outcome
	 * @param outcome - new value
	 */
	 public void setOutcome( String  __outcome ){
		this._outcome = __outcome;
     }
	 
   	/*
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	/**
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 * Setter for accesskey
	 * @param accesskey - new value
	 */
	 public void setAccesskey( String  __accesskey ){
		this._accesskey = __accesskey;
     }
	 
   	/*
	 * fragment
	 * the fragment identifier to link to.
	 */
	/**
	 * the fragment identifier to link to.
	 * Setter for fragment
	 * @param fragment - new value
	 */
	 public void setFragment( String  __fragment ){
		this._fragment = __fragment;
     }
	 
   	/*
	 * onkeypress
	 * HTML: a script expression; a key is pressed and released
	 */
	/**
	 * HTML: a script expression; a key is pressed and released
	 * Setter for onkeypress
	 * @param onkeypress - new value
	 */
	 public void setOnkeypress( String  __onkeypress ){
		this._onkeypress = __onkeypress;
     }
	 
   	/*
	 * ondblclick
	 * HTML: a script expression; a pointer button is double-clicked
	 */
	/**
	 * HTML: a script expression; a pointer button is double-clicked
	 * Setter for ondblclick
	 * @param ondblclick - new value
	 */
	 public void setOndblclick( String  __ondblclick ){
		this._ondblclick = __ondblclick;
     }
	 
   	/*
	 * image
	 * image
	 */
	/**
	 * image
	 * Setter for image
	 * @param image - new value
	 */
	 public void setImage( String  __image ){
		this._image = __image;
     }
	 
   	/*
	 * propagation
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	/**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Setter for propagation
	 * @param propagation - new value
	 */
	 public void setPropagation( String  __propagation ){
		this._propagation = __propagation;
     }
	 
   	/*
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	/**
	 * CSS style(s) is/are to be applied when this component is rendered
	 * Setter for style
	 * @param style - new value
	 */
	 public void setStyle( String  __style ){
		this._style = __style;
     }
	 
     	/*
	 * size
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 */
	/**
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 * Setter for size
	 * @param size - new value
	 */
	 public void setSize( String  __size ){
		this._size = __size;
     }
	 
   	/*
	 * onmouseover
	 * HTML: a script expression; a pointer is moved onto
	 */
	/**
	 * HTML: a script expression; a pointer is moved onto
	 * Setter for onmouseover
	 * @param onmouseover - new value
	 */
	 public void setOnmouseover( String  __onmouseover ){
		this._onmouseover = __onmouseover;
     }
	 
   	/*
	 * view
	 * the JSF view id to link to.
	 */
	/**
	 * the JSF view id to link to.
	 * Setter for view
	 * @param view - new value
	 */
	 public void setView( String  __view ){
		this._view = __view;
     }
	 
   	/*
	 * action
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 */
	/**
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 * Setter for action
	 * @param action - new value
	 */
	 public void setAction( String  __action ){
		this._action = __action;
     }
	 
     	/*
	 * onkeyup
	 * HTML: a script expression; a key is released
	 */
	/**
	 * HTML: a script expression; a key is released
	 * Setter for onkeyup
	 * @param onkeyup - new value
	 */
	 public void setOnkeyup( String  __onkeyup ){
		this._onkeyup = __onkeyup;
     }
	 
   	/*
	 * tabindex
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 */
	/**
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 * Setter for tabindex
	 * @param tabindex - new value
	 */
	 public void setTabindex( String  __tabindex ){
		this._tabindex = __tabindex;
     }
	 
     	/*
	 * type
	 * submit|reset|image|button This attribute specifies a type of control to create. The default value for this attribute is "submit"
	 */
	/**
	 * submit|reset|image|button This attribute specifies a type of control to create. The default value for this attribute is "submit"
	 * Setter for type
	 * @param type - new value
	 */
	 public void setType( String  __type ){
		this._type = __type;
     }
	 
   	/*
	 * lang
	 * Code describing the language used in the generated markup for this component
	 */
	/**
	 * Code describing the language used in the generated markup for this component
	 * Setter for lang
	 * @param lang - new value
	 */
	 public void setLang( String  __lang ){
		this._lang = __lang;
     }
	 
   	/*
	 * disabled
	 * When set for a form control, this boolean attribute disables the control for user input.
	 */
	/**
	 * When set for a form control, this boolean attribute disables the control for user input.
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( String  __disabled ){
		this._disabled = __disabled;
     }
	 
   	/*
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	/**
	 * HTML: a script expression; a pointer button is clicked
	 * Setter for onclick
	 * @param onclick - new value
	 */
	 public void setOnclick( String  __onclick ){
		this._onclick = __onclick;
     }
	 
     	/*
	 * alt
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 */
	/**
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 * Setter for alt
	 * @param alt - new value
	 */
	 public void setAlt( String  __alt ){
		this._alt = __alt;
     }
	 
   	/*
	 * onmouseout
	 * HTML: a script expression; a pointer is moved away
	 */
	/**
	 * HTML: a script expression; a pointer is moved away
	 * Setter for onmouseout
	 * @param onmouseout - new value
	 */
	 public void setOnmouseout( String  __onmouseout ){
		this._onmouseout = __onmouseout;
     }
	 
   	/*
	 * onkeydown
	 * HTML: a script expression; a key is pressed down
	 */
	/**
	 * HTML: a script expression; a key is pressed down
	 * Setter for onkeydown
	 * @param onkeydown - new value
	 */
	 public void setOnkeydown( String  __onkeydown ){
		this._onkeydown = __onkeydown;
     }
	 
   	/*
	 * onmousedown
	 * HTML: script expression; a pointer button is pressed down
	 */
	/**
	 * HTML: script expression; a pointer button is pressed down
	 * Setter for onmousedown
	 * @param onmousedown - new value
	 */
	 public void setOnmousedown( String  __onmousedown ){
		this._onmousedown = __onmousedown;
     }
	 
   	/*
	 * immediate
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 */
	/**
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 * Setter for immediate
	 * @param immediate - new value
	 */
	 public void setImmediate( String  __immediate ){
		this._immediate = __immediate;
     }
	 
   	/*
	 * onmouseup
	 * HTML: script expression; a pointer button is released
	 */
	/**
	 * HTML: script expression; a pointer button is released
	 * Setter for onmouseup
	 * @param onmouseup - new value
	 */
	 public void setOnmouseup( String  __onmouseup ){
		this._onmouseup = __onmouseup;
     }
	 
       	/*
	 * onmousemove
	 * HTML: a script expression; a pointer is moved within
	 */
	/**
	 * HTML: a script expression; a pointer is moved within
	 * Setter for onmousemove
	 * @param onmousemove - new value
	 */
	 public void setOnmousemove( String  __onmousemove ){
		this._onmousemove = __onmousemove;
     }
	 
  // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._actionListener = null;
    	    this._title = null;
     	    this._dir = null;
  	    this._styleClass = null;
  	    this._pageflow = null;
  	    this._outcome = null;
  	    this._accesskey = null;
  	    this._fragment = null;
  	    this._onkeypress = null;
  	    this._ondblclick = null;
  	    this._image = null;
  	    this._propagation = null;
  	    this._style = null;
   	    this._size = null;
  	    this._onmouseover = null;
  	    this._view = null;
  	    this._action = null;
   	    this._onkeyup = null;
  	    this._tabindex = null;
   	    this._type = null;
  	    this._lang = null;
  	    this._disabled = null;
  	    this._onclick = null;
   	    this._alt = null;
  	    this._onmouseout = null;
  	    this._onkeydown = null;
  	    this._onmousedown = null;
  	    this._immediate = null;
  	    this._onmouseup = null;
    	    this._onmousemove = null;
 	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			setActionListenerProperty(component, this._actionListener);
		     		 		 			setStringProperty(component, "title",this._title);
		      		 		 			setStringProperty(component, "dir",this._dir);
		   		 		 			setStringProperty(component, "styleClass",this._styleClass);
		   		 		 			setStringProperty(component, "pageflow",this._pageflow);
		   		 		 			setStringProperty(component, "outcome",this._outcome);
		   		 		 			setStringProperty(component, "accesskey",this._accesskey);
		   		 		 			setStringProperty(component, "fragment",this._fragment);
		   		 		 			setStringProperty(component, "onkeypress",this._onkeypress);
		   		 		 			setStringProperty(component, "ondblclick",this._ondblclick);
		   		 		 			setStringProperty(component, "image",this._image);
		   		 		 			setStringProperty(component, "propagation",this._propagation);
		   		 		 			setStringProperty(component, "style",this._style);
		    		 		 			// Simple type - int
			setIntegerProperty(component, "size",this._size); 
		   		 		 			setStringProperty(component, "onmouseover",this._onmouseover);
		   		 		 			setStringProperty(component, "view",this._view);
		   		 		 			setActionProperty(component, this._action);
		    		 		 			setStringProperty(component, "onkeyup",this._onkeyup);
		   		 		 			setStringProperty(component, "tabindex",this._tabindex);
		    		 		 			setStringProperty(component, "type",this._type);
		   		 		 			setStringProperty(component, "lang",this._lang);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "disabled",this._disabled); 
		   		 		 			setStringProperty(component, "onclick",this._onclick);
		    		 		 			setStringProperty(component, "alt",this._alt);
		   		 		 			setStringProperty(component, "onmouseout",this._onmouseout);
		   		 		 			setStringProperty(component, "onkeydown",this._onkeydown);
		   		 		 			setStringProperty(component, "onmousedown",this._onmousedown);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "immediate",this._immediate); 
		   		 		 			setStringProperty(component, "onmouseup",this._onmouseup);
		     		 		 			setStringProperty(component, "onmousemove",this._onmousemove);
		     }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.Button";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.ButtonRenderer";
			}

}
