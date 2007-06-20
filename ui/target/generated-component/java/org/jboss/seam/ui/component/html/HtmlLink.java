/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import org.jboss.seam.ui.component.UILink ;
import javax.faces.el.MethodBinding ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.Link
 * Component-Family org.jboss.seam.ui.Link
  	 * Renderer-Type org.jboss.seam.ui.LinkRenderer
  	 * A link that supports invocation of an action with control over conversation propagation. Does not submit the form.
 */
 public class HtmlLink extends org.jboss.seam.ui.component.UILink {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.Link";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlLink (){
  	  	setRendererType("org.jboss.seam.ui.LinkRenderer");
  	  }

// Component properties fields
 	/**
	 * actionListener
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 */
	 	 private MethodBinding  _actionListener = null; /* Default is null*/
	 	    	/**
	 * title
	 * Advisory title information about markup elements generated for this component
	 */
	 	 private String  _title = null; /* Default is null*/
	 	   	/**
	 * shape
	 * default|rect|circle|poly [CI]
            This attribute specifies the shape of a region. Possible values:
            
            * default: Specifies the entire region.
            * rect: Define a rectangular region.
            * circle: Define a circular region.
            * poly: Define a polygonal region.
	 */
	 	 private String  _shape = null; /* Default is null*/
	 	  	/**
	 * target
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 */
	 	 private String  _target = null; /* Default is null*/
	 	  	/**
	 * charset
	 * The character encoding of a resource designated by this hyperlink
	 */
	 	 private String  _charset = null; /* Default is null*/
	 	    	/**
	 * dir
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 */
	 	 private String  _dir = null; /* Default is null*/
	 	  	/**
	 * rev
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 */
	 	 private String  _rev = null; /* Default is null*/
	 	  	/**
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 	 private String  _styleClass = null; /* Default is null*/
	 	  	/**
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	 	 private String  _pageflow = null; /* Default is null*/
	 	  	/**
	 * outcome
	 * outcome
	 */
	 	 private String  _outcome = null; /* Default is null*/
	 	  	/**
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	 	 private String  _accesskey = null; /* Default is null*/
	 	  	/**
	 * fragment
	 * the fragment identifier to link to.
	 */
	 	 private String  _fragment = null; /* Default is null*/
	 	  	/**
	 * onkeypress
	 * HTML: a script expression; a key is pressed and released
	 */
	 	 private String  _onkeypress = null; /* Default is null*/
	 	  	/**
	 * ondblclick
	 * HTML: a script expression; a pointer button is double-clicked
	 */
	 	 private String  _ondblclick = null; /* Default is null*/
	 	  	/**
	 * propagation
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	 	 private String  _propagation = null; /* Default is "default"*/
	 	  	/**
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 	 private String  _style = null; /* Default is null*/
	 	   	/**
	 * onmouseover
	 * HTML: a script expression; a pointer is moved onto
	 */
	 	 private String  _onmouseover = null; /* Default is null*/
	 	  	/**
	 * onblur
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 */
	 	 private String  _onblur = null; /* Default is null*/
	 	  	/**
	 * view
	 * the JSF view id to link to.
	 */
	 	 private String  _view = null; /* Default is null*/
	 	  	/**
	 * action
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 */
	 	 private MethodBinding  _action = null; /* Default is null*/
	 	  	/**
	 * onkeyup
	 * HTML: a script expression; a key is released
	 */
	 	 private String  _onkeyup = null; /* Default is null*/
	 	   	/**
	 * tabindex
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 */
	 	 private String  _tabindex = null; /* Default is null*/
	 	  	/**
	 * hreflang
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 */
	 	 private String  _hreflang = null; /* Default is null*/
	 	   	/**
	 * type
	 * The content type of the resource designated by this hyperlink
	 */
	 	 private String  _type = null; /* Default is null*/
	 	  	/**
	 * lang
	 * Code describing the language used in the generated markup for this component
	 */
	 	 private String  _lang = null; /* Default is null*/
	 	  	/**
	 * disabled
	 * disabled
	 */
	 	 private boolean  _disabled = false;		
	/**
	 * Flag indicated what disabled is set.
	 */
	 private boolean _disabledSet = false;	
	 	  	/**
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	 	 private String  _onclick = null; /* Default is null*/
	 	   	/**
	 * onmouseout
	 * HTML: a script expression; a pointer is moved away
	 */
	 	 private String  _onmouseout = null; /* Default is null*/
	 	  	/**
	 * onkeydown
	 * HTML: a script expression; a key is pressed down
	 */
	 	 private String  _onkeydown = null; /* Default is null*/
	 	  	/**
	 * onmousedown
	 * HTML: script expression; a pointer button is pressed down
	 */
	 	 private String  _onmousedown = null; /* Default is null*/
	 	  	/**
	 * rel
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 */
	 	 private String  _rel = null; /* Default is null*/
	 	  	/**
	 * immediate
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 */
	 	 private boolean  _immediate = false;		
	/**
	 * Flag indicated what immediate is set.
	 */
	 private boolean _immediateSet = false;	
	 	  	/**
	 * onfocus
	 * JavaScript code. The onfocus event occurs when an element gets focus.
	 */
	 	 private String  _onfocus = null; /* Default is null*/
	 	  	/**
	 * onmouseup
	 * HTML: script expression; a pointer button is released
	 */
	 	 private String  _onmouseup = null; /* Default is null*/
	 	    	/**
	 * onmousemove
	 * HTML: a script expression; a pointer is moved within
	 */
	 	 private String  _onmousemove = null; /* Default is null*/
	 	  	/**
	 * coords
	 * This attribute specifies the position and shape on the screen. The number and order of values depends on the shape being defined. Possible combinations:
            
            * rect: left-x, top-y, right-x, bottom-y.
            * circle: center-x, center-y, radius. Note. When the radius value is percentage value, user agents should calculate the final radius value based on the associated object's width and height. The radius should be the smaller value of the two.
            * poly: x1, y1, x2, y2, ..., xN, yN. The first x and y coordinate pair and the last should be the same to close the polygon. When these coordinate values are not the same, user agents should infer an additional coordinate pair to close the polygon.
            
            Coordinates are relative to the top, left corner of the object. All values are lengths. All values are separated by commas
	 */
	 	 private String  _coords = null; /* Default is null*/
	 	 
// Getters-setters
    /**
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 * Setter for actionListener
	 * @param actionListener - new value
	 */
	 public void setActionListener( MethodBinding  __actionListener ){
		this._actionListener = __actionListener;
	 	 }


   /**
	 * MethodBinding pointing at method accepting  an ActionEvent with return type void
	 * Getter for actionListener
	 * @return actionListener value from local variable or value bindings
	 */
	 public MethodBinding getActionListener(  ){
	         if (null != this._actionListener)
        {
            return this._actionListener;
                } else {
            return null;
        }
	 	 }
	        /**
	 * Advisory title information about markup elements generated for this component
	 * Setter for title
	 * @param title - new value
	 */
	 public void setTitle( String  __title ){
		this._title = __title;
	 	 }


   /**
	 * Advisory title information about markup elements generated for this component
	 * Getter for title
	 * @return title value from local variable or value bindings
	 */
	 public String getTitle(  ){
	         if (null != this._title)
        {
            return this._title;
        	    }
        ValueBinding vb = getValueBinding("title");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * default|rect|circle|poly [CI]
            This attribute specifies the shape of a region. Possible values:
            
            * default: Specifies the entire region.
            * rect: Define a rectangular region.
            * circle: Define a circular region.
            * poly: Define a polygonal region.
	 * Setter for shape
	 * @param shape - new value
	 */
	 public void setShape( String  __shape ){
		this._shape = __shape;
	 	 }


   /**
	 * default|rect|circle|poly [CI]
            This attribute specifies the shape of a region. Possible values:
            
            * default: Specifies the entire region.
            * rect: Define a rectangular region.
            * circle: Define a circular region.
            * poly: Define a polygonal region.
	 * Getter for shape
	 * @return shape value from local variable or value bindings
	 */
	 public String getShape(  ){
	         if (null != this._shape)
        {
            return this._shape;
        	    }
        ValueBinding vb = getValueBinding("shape");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 * Setter for target
	 * @param target - new value
	 */
	 public void setTarget( String  __target ){
		this._target = __target;
	 	 }


   /**
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 * Getter for target
	 * @return target value from local variable or value bindings
	 */
	 public String getTarget(  ){
	         if (null != this._target)
        {
            return this._target;
        	    }
        ValueBinding vb = getValueBinding("target");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * The character encoding of a resource designated by this hyperlink
	 * Setter for charset
	 * @param charset - new value
	 */
	 public void setCharset( String  __charset ){
		this._charset = __charset;
	 	 }


   /**
	 * The character encoding of a resource designated by this hyperlink
	 * Getter for charset
	 * @return charset value from local variable or value bindings
	 */
	 public String getCharset(  ){
	         if (null != this._charset)
        {
            return this._charset;
        	    }
        ValueBinding vb = getValueBinding("charset");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
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


   /**
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 * Getter for dir
	 * @return dir value from local variable or value bindings
	 */
	 public String getDir(  ){
	         if (null != this._dir)
        {
            return this._dir;
        	    }
        ValueBinding vb = getValueBinding("dir");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 * Setter for rev
	 * @param rev - new value
	 */
	 public void setRev( String  __rev ){
		this._rev = __rev;
	 	 }


   /**
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 * Getter for rev
	 * @return rev value from local variable or value bindings
	 */
	 public String getRev(  ){
	         if (null != this._rev)
        {
            return this._rev;
        	    }
        ValueBinding vb = getValueBinding("rev");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
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
	 * outcome
	 * Setter for outcome
	 * @param outcome - new value
	 */
	 public void setOutcome( String  __outcome ){
		this._outcome = __outcome;
	 	 }


   /**
	 * outcome
	 * Getter for outcome
	 * @return outcome value from local variable or value bindings
	 */
	 public String getOutcome(  ){
	         if (null != this._outcome)
        {
            return this._outcome;
        	    }
        ValueBinding vb = getValueBinding("outcome");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 * Setter for accesskey
	 * @param accesskey - new value
	 */
	 public void setAccesskey( String  __accesskey ){
		this._accesskey = __accesskey;
	 	 }


   /**
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 * Getter for accesskey
	 * @return accesskey value from local variable or value bindings
	 */
	 public String getAccesskey(  ){
	         if (null != this._accesskey)
        {
            return this._accesskey;
        	    }
        ValueBinding vb = getValueBinding("accesskey");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * the fragment identifier to link to.
	 * Setter for fragment
	 * @param fragment - new value
	 */
	 public void setFragment( String  __fragment ){
		this._fragment = __fragment;
	 	 }


   /**
	 * the fragment identifier to link to.
	 * Getter for fragment
	 * @return fragment value from local variable or value bindings
	 */
	 public String getFragment(  ){
	         if (null != this._fragment)
        {
            return this._fragment;
        	    }
        ValueBinding vb = getValueBinding("fragment");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: a script expression; a key is pressed and released
	 * Setter for onkeypress
	 * @param onkeypress - new value
	 */
	 public void setOnkeypress( String  __onkeypress ){
		this._onkeypress = __onkeypress;
	 	 }


   /**
	 * HTML: a script expression; a key is pressed and released
	 * Getter for onkeypress
	 * @return onkeypress value from local variable or value bindings
	 */
	 public String getOnkeypress(  ){
	         if (null != this._onkeypress)
        {
            return this._onkeypress;
        	    }
        ValueBinding vb = getValueBinding("onkeypress");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: a script expression; a pointer button is double-clicked
	 * Setter for ondblclick
	 * @param ondblclick - new value
	 */
	 public void setOndblclick( String  __ondblclick ){
		this._ondblclick = __ondblclick;
	 	 }


   /**
	 * HTML: a script expression; a pointer button is double-clicked
	 * Getter for ondblclick
	 * @return ondblclick value from local variable or value bindings
	 */
	 public String getOndblclick(  ){
	         if (null != this._ondblclick)
        {
            return this._ondblclick;
        	    }
        ValueBinding vb = getValueBinding("ondblclick");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Setter for propagation
	 * @param propagation - new value
	 */
	 public void setPropagation( String  __propagation ){
		this._propagation = __propagation;
	 	 }


   /**
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 * Getter for propagation
	 * @return propagation value from local variable or value bindings
	 */
	 public String getPropagation(  ){
	         if (null != this._propagation)
        {
            return this._propagation;
        	    }
        ValueBinding vb = getValueBinding("propagation");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return "default";
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
	       /**
	 * HTML: a script expression; a pointer is moved onto
	 * Setter for onmouseover
	 * @param onmouseover - new value
	 */
	 public void setOnmouseover( String  __onmouseover ){
		this._onmouseover = __onmouseover;
	 	 }


   /**
	 * HTML: a script expression; a pointer is moved onto
	 * Getter for onmouseover
	 * @return onmouseover value from local variable or value bindings
	 */
	 public String getOnmouseover(  ){
	         if (null != this._onmouseover)
        {
            return this._onmouseover;
        	    }
        ValueBinding vb = getValueBinding("onmouseover");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 * Setter for onblur
	 * @param onblur - new value
	 */
	 public void setOnblur( String  __onblur ){
		this._onblur = __onblur;
	 	 }


   /**
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 * Getter for onblur
	 * @return onblur value from local variable or value bindings
	 */
	 public String getOnblur(  ){
	         if (null != this._onblur)
        {
            return this._onblur;
        	    }
        ValueBinding vb = getValueBinding("onblur");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * the JSF view id to link to.
	 * Setter for view
	 * @param view - new value
	 */
	 public void setView( String  __view ){
		this._view = __view;
	 	 }


   /**
	 * the JSF view id to link to.
	 * Getter for view
	 * @return view value from local variable or value bindings
	 */
	 public String getView(  ){
	         if (null != this._view)
        {
            return this._view;
        	    }
        ValueBinding vb = getValueBinding("view");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 * Setter for action
	 * @param action - new value
	 */
	 public void setAction( MethodBinding  __action ){
		this._action = __action;
	 	 }


   /**
	 * MethodBinding pointing at the application action to be invoked,
            if this UIComponent is activated by the user, during the Apply
            Request Values or Invoke Application phase of the request
            processing lifecycle, depending on the value of the immediate
            property
	 * Getter for action
	 * @return action value from local variable or value bindings
	 */
	 public MethodBinding getAction(  ){
	         if (null != this._action)
        {
            return this._action;
                } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: a script expression; a key is released
	 * Setter for onkeyup
	 * @param onkeyup - new value
	 */
	 public void setOnkeyup( String  __onkeyup ){
		this._onkeyup = __onkeyup;
	 	 }


   /**
	 * HTML: a script expression; a key is released
	 * Getter for onkeyup
	 * @return onkeyup value from local variable or value bindings
	 */
	 public String getOnkeyup(  ){
	         if (null != this._onkeyup)
        {
            return this._onkeyup;
        	    }
        ValueBinding vb = getValueBinding("onkeyup");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 * Setter for tabindex
	 * @param tabindex - new value
	 */
	 public void setTabindex( String  __tabindex ){
		this._tabindex = __tabindex;
	 	 }


   /**
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 * Getter for tabindex
	 * @return tabindex value from local variable or value bindings
	 */
	 public String getTabindex(  ){
	         if (null != this._tabindex)
        {
            return this._tabindex;
        	    }
        ValueBinding vb = getValueBinding("tabindex");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 * Setter for hreflang
	 * @param hreflang - new value
	 */
	 public void setHreflang( String  __hreflang ){
		this._hreflang = __hreflang;
	 	 }


   /**
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 * Getter for hreflang
	 * @return hreflang value from local variable or value bindings
	 */
	 public String getHreflang(  ){
	         if (null != this._hreflang)
        {
            return this._hreflang;
        	    }
        ValueBinding vb = getValueBinding("hreflang");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * The content type of the resource designated by this hyperlink
	 * Setter for type
	 * @param type - new value
	 */
	 public void setType( String  __type ){
		this._type = __type;
	 	 }


   /**
	 * The content type of the resource designated by this hyperlink
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
            return null;
        }
	 	 }
	      /**
	 * Code describing the language used in the generated markup for this component
	 * Setter for lang
	 * @param lang - new value
	 */
	 public void setLang( String  __lang ){
		this._lang = __lang;
	 	 }


   /**
	 * Code describing the language used in the generated markup for this component
	 * Getter for lang
	 * @return lang value from local variable or value bindings
	 */
	 public String getLang(  ){
	         if (null != this._lang)
        {
            return this._lang;
        	    }
        ValueBinding vb = getValueBinding("lang");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * disabled
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( boolean  __disabled ){
		this._disabled = __disabled;
	 		this._disabledSet = true;
	 	 }


   /**
	 * disabled
	 * Getter for disabled
	 * @return disabled value from local variable or value bindings
	 */
	 public boolean isDisabled(  ){
	 		 if(this._disabledSet){
			return this._disabled;
		 }
    	ValueBinding vb = getValueBinding("disabled");
    	if (vb != null) {
    	    Boolean value = (Boolean) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._disabled;
    	    }
    	    return (value.booleanValue());
    	} else {
    	    return (this._disabled);
    	}
	 	 }
	      /**
	 * HTML: a script expression; a pointer button is clicked
	 * Setter for onclick
	 * @param onclick - new value
	 */
	 public void setOnclick( String  __onclick ){
		this._onclick = __onclick;
	 	 }


   /**
	 * HTML: a script expression; a pointer button is clicked
	 * Getter for onclick
	 * @return onclick value from local variable or value bindings
	 */
	 public String getOnclick(  ){
	         if (null != this._onclick)
        {
            return this._onclick;
        	    }
        ValueBinding vb = getValueBinding("onclick");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * HTML: a script expression; a pointer is moved away
	 * Setter for onmouseout
	 * @param onmouseout - new value
	 */
	 public void setOnmouseout( String  __onmouseout ){
		this._onmouseout = __onmouseout;
	 	 }


   /**
	 * HTML: a script expression; a pointer is moved away
	 * Getter for onmouseout
	 * @return onmouseout value from local variable or value bindings
	 */
	 public String getOnmouseout(  ){
	         if (null != this._onmouseout)
        {
            return this._onmouseout;
        	    }
        ValueBinding vb = getValueBinding("onmouseout");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: a script expression; a key is pressed down
	 * Setter for onkeydown
	 * @param onkeydown - new value
	 */
	 public void setOnkeydown( String  __onkeydown ){
		this._onkeydown = __onkeydown;
	 	 }


   /**
	 * HTML: a script expression; a key is pressed down
	 * Getter for onkeydown
	 * @return onkeydown value from local variable or value bindings
	 */
	 public String getOnkeydown(  ){
	         if (null != this._onkeydown)
        {
            return this._onkeydown;
        	    }
        ValueBinding vb = getValueBinding("onkeydown");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: script expression; a pointer button is pressed down
	 * Setter for onmousedown
	 * @param onmousedown - new value
	 */
	 public void setOnmousedown( String  __onmousedown ){
		this._onmousedown = __onmousedown;
	 	 }


   /**
	 * HTML: script expression; a pointer button is pressed down
	 * Getter for onmousedown
	 * @return onmousedown value from local variable or value bindings
	 */
	 public String getOnmousedown(  ){
	         if (null != this._onmousedown)
        {
            return this._onmousedown;
        	    }
        ValueBinding vb = getValueBinding("onmousedown");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 * Setter for rel
	 * @param rel - new value
	 */
	 public void setRel( String  __rel ){
		this._rel = __rel;
	 	 }


   /**
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 * Getter for rel
	 * @return rel value from local variable or value bindings
	 */
	 public String getRel(  ){
	         if (null != this._rel)
        {
            return this._rel;
        	    }
        ValueBinding vb = getValueBinding("rel");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 * Setter for immediate
	 * @param immediate - new value
	 */
	 public void setImmediate( boolean  __immediate ){
		this._immediate = __immediate;
	 		this._immediateSet = true;
	 	 }


   /**
	 * True means, that the default ActionListener should be executed
            immediately (i.e. during Apply Request Values phase of the
            request processing lifecycle), rather than waiting until the
            Invoke Application phase
	 * Getter for immediate
	 * @return immediate value from local variable or value bindings
	 */
	 public boolean isImmediate(  ){
	 		 if(this._immediateSet){
			return this._immediate;
		 }
    	ValueBinding vb = getValueBinding("immediate");
    	if (vb != null) {
    	    Boolean value = (Boolean) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._immediate;
    	    }
    	    return (value.booleanValue());
    	} else {
    	    return (this._immediate);
    	}
	 	 }
	      /**
	 * JavaScript code. The onfocus event occurs when an element gets focus.
	 * Setter for onfocus
	 * @param onfocus - new value
	 */
	 public void setOnfocus( String  __onfocus ){
		this._onfocus = __onfocus;
	 	 }


   /**
	 * JavaScript code. The onfocus event occurs when an element gets focus.
	 * Getter for onfocus
	 * @return onfocus value from local variable or value bindings
	 */
	 public String getOnfocus(  ){
	         if (null != this._onfocus)
        {
            return this._onfocus;
        	    }
        ValueBinding vb = getValueBinding("onfocus");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: script expression; a pointer button is released
	 * Setter for onmouseup
	 * @param onmouseup - new value
	 */
	 public void setOnmouseup( String  __onmouseup ){
		this._onmouseup = __onmouseup;
	 	 }


   /**
	 * HTML: script expression; a pointer button is released
	 * Getter for onmouseup
	 * @return onmouseup value from local variable or value bindings
	 */
	 public String getOnmouseup(  ){
	         if (null != this._onmouseup)
        {
            return this._onmouseup;
        	    }
        ValueBinding vb = getValueBinding("onmouseup");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	        /**
	 * HTML: a script expression; a pointer is moved within
	 * Setter for onmousemove
	 * @param onmousemove - new value
	 */
	 public void setOnmousemove( String  __onmousemove ){
		this._onmousemove = __onmousemove;
	 	 }


   /**
	 * HTML: a script expression; a pointer is moved within
	 * Getter for onmousemove
	 * @return onmousemove value from local variable or value bindings
	 */
	 public String getOnmousemove(  ){
	         if (null != this._onmousemove)
        {
            return this._onmousemove;
        	    }
        ValueBinding vb = getValueBinding("onmousemove");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * This attribute specifies the position and shape on the screen. The number and order of values depends on the shape being defined. Possible combinations:
            
            * rect: left-x, top-y, right-x, bottom-y.
            * circle: center-x, center-y, radius. Note. When the radius value is percentage value, user agents should calculate the final radius value based on the associated object's width and height. The radius should be the smaller value of the two.
            * poly: x1, y1, x2, y2, ..., xN, yN. The first x and y coordinate pair and the last should be the same to close the polygon. When these coordinate values are not the same, user agents should infer an additional coordinate pair to close the polygon.
            
            Coordinates are relative to the top, left corner of the object. All values are lengths. All values are separated by commas
	 * Setter for coords
	 * @param coords - new value
	 */
	 public void setCoords( String  __coords ){
		this._coords = __coords;
	 	 }


   /**
	 * This attribute specifies the position and shape on the screen. The number and order of values depends on the shape being defined. Possible combinations:
            
            * rect: left-x, top-y, right-x, bottom-y.
            * circle: center-x, center-y, radius. Note. When the radius value is percentage value, user agents should calculate the final radius value based on the associated object's width and height. The radius should be the smaller value of the two.
            * poly: x1, y1, x2, y2, ..., xN, yN. The first x and y coordinate pair and the last should be the same to close the polygon. When these coordinate values are not the same, user agents should infer an additional coordinate pair to close the polygon.
            
            Coordinates are relative to the top, left corner of the object. All values are lengths. All values are separated by commas
	 * Getter for coords
	 * @return coords value from local variable or value bindings
	 */
	 public String getCoords(  ){
	         if (null != this._coords)
        {
            return this._coords;
        	    }
        ValueBinding vb = getValueBinding("coords");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	  
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Link";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[39];
        values[0] = super.saveState(context);
 	 	          values[1] = saveAttachedState(context, _actionListener );		
	   	 	
    	 	          values[2] = _title;
	   	 	
   	 	          values[3] = _shape;
	   	 	
  	 	          values[4] = _target;
	   	 	
  	 	          values[5] = _charset;
	   	 	
    	 	          values[6] = _dir;
	   	 	
  	 	          values[7] = _rev;
	   	 	
  	 	          values[8] = _styleClass;
	   	 	
  	 	          values[9] = _pageflow;
	   	 	
  	 	          values[10] = _outcome;
	   	 	
  	 	          values[11] = _accesskey;
	   	 	
  	 	          values[12] = _fragment;
	   	 	
  	 	          values[13] = _onkeypress;
	   	 	
  	 	          values[14] = _ondblclick;
	   	 	
  	 	          values[15] = _propagation;
	   	 	
  	 	          values[16] = _style;
	   	 	
   	 	          values[17] = _onmouseover;
	   	 	
  	 	          values[18] = _onblur;
	   	 	
  	 	          values[19] = _view;
	   	 	
  	 	          values[20] = saveAttachedState(context, _action );		
	   	 	
  	 	          values[21] = _onkeyup;
	   	 	
   	 	          values[22] = _tabindex;
	   	 	
  	 	          values[23] = _hreflang;
	   	 	
   	 	          values[24] = _type;
	   	 	
  	 	          values[25] = _lang;
	   	 	
  	        values[26] = new Boolean(_disabled);
	   	   values[27] = Boolean.valueOf(_disabledSet);	
	   	 	
  	 	          values[28] = _onclick;
	   	 	
   	 	          values[29] = _onmouseout;
	   	 	
  	 	          values[30] = _onkeydown;
	   	 	
  	 	          values[31] = _onmousedown;
	   	 	
  	 	          values[32] = _rel;
	   	 	
  	        values[33] = new Boolean(_immediate);
	   	   values[34] = Boolean.valueOf(_immediateSet);	
	   	 	
  	 	          values[35] = _onfocus;
	   	 	
  	 	          values[36] = _onmouseup;
	   	 	
    	 	          values[37] = _onmousemove;
	   	 	
  	 	          values[38] = _coords;
	   	 	
 	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _actionListener = (MethodBinding)restoreAttachedState(context,values[1] );		
	   	 	
    	 	          _title = (String)values[2] ;
	   	 	
   	 	          _shape = (String)values[3] ;
	   	 	
  	 	          _target = (String)values[4] ;
	   	 	
  	 	          _charset = (String)values[5] ;
	   	 	
    	 	          _dir = (String)values[6] ;
	   	 	
  	 	          _rev = (String)values[7] ;
	   	 	
  	 	          _styleClass = (String)values[8] ;
	   	 	
  	 	          _pageflow = (String)values[9] ;
	   	 	
  	 	          _outcome = (String)values[10] ;
	   	 	
  	 	          _accesskey = (String)values[11] ;
	   	 	
  	 	          _fragment = (String)values[12] ;
	   	 	
  	 	          _onkeypress = (String)values[13] ;
	   	 	
  	 	          _ondblclick = (String)values[14] ;
	   	 	
  	 	          _propagation = (String)values[15] ;
	   	 	
  	 	          _style = (String)values[16] ;
	   	 	
   	 	          _onmouseover = (String)values[17] ;
	   	 	
  	 	          _onblur = (String)values[18] ;
	   	 	
  	 	          _view = (String)values[19] ;
	   	 	
  	 	          _action = (MethodBinding)restoreAttachedState(context,values[20] );		
	   	 	
  	 	          _onkeyup = (String)values[21] ;
	   	 	
   	 	          _tabindex = (String)values[22] ;
	   	 	
  	 	          _hreflang = (String)values[23] ;
	   	 	
   	 	          _type = (String)values[24] ;
	   	 	
  	 	          _lang = (String)values[25] ;
	   	 	
  	        _disabled = ((Boolean)values[26]).booleanValue();
	   	   _disabledSet = ((Boolean)values[27]).booleanValue();	
	   	 	
  	 	          _onclick = (String)values[28] ;
	   	 	
   	 	          _onmouseout = (String)values[29] ;
	   	 	
  	 	          _onkeydown = (String)values[30] ;
	   	 	
  	 	          _onmousedown = (String)values[31] ;
	   	 	
  	 	          _rel = (String)values[32] ;
	   	 	
  	        _immediate = ((Boolean)values[33]).booleanValue();
	   	   _immediateSet = ((Boolean)values[34]).booleanValue();	
	   	 	
  	 	          _onfocus = (String)values[35] ;
	   	 	
  	 	          _onmouseup = (String)values[36] ;
	   	 	
    	 	          _onmousemove = (String)values[37] ;
	   	 	
  	 	          _coords = (String)values[38] ;
	   	 	
 	
		
	}	
// Utilites

}