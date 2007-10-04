/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.convert.Converter ;
import java.lang.Object ;
import javax.el.MethodExpression ;
import javax.faces.el.MethodBinding ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlLink;

public class LinkTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
  	/*
	 * dir
	 * Direction indication for text that does not inherit
			directionality. Valid values are "LTR" (left-to-right)
			and "RTL" (right-to-left)
	 */
	 private String  _dir = null;

   	/*
	 * rev
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 */
	 private String  _rev = null;

  	/*
	 * pageflow
	 * a pageflow definition to begin. (This is only useful when propagation="begin" or propagation="join".)
	 */
	 private String  _pageflow = null;

  	/*
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 private String  _style = null;

  	/*
	 * propagation
	 * determines the conversation propagation style: begin, join, nest, none or end.
	 */
	 private String  _propagation = null;

  	/*
	 * onmouseover
	 * HTML: a script expression; a pointer is moved onto
	 */
	 private String  _onmouseover = null;

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
	 * lang
	 * Code describing the language used in the generated markup for this component
	 */
	 private String  _lang = null;

  	/*
	 * actionExpression
	 * actionExpression
	 */
	 private String  _actionExpression = null;

  	/*
	 * disabled
	 * disabled
	 */
	 private String  _disabled = null;

   	/*
	 * onmouseout
	 * HTML: a script expression; a pointer is moved away
	 */
	 private String  _onmouseout = null;

  	/*
	 * taskInstance
	 * Specify the task to operate on (e.g. for @StartTask)
	 */
	 private String  _taskInstance = null;

  	/*
	 * rel
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 */
	 private String  _rel = null;

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

  	/*
	 * coords
	 * This attribute specifies the position and shape on the screen. The number and order of values depends on the shape being defined. Possible combinations:
            
            * rect: left-x, top-y, right-x, bottom-y.
            * circle: center-x, center-y, radius. Note. When the radius value is percentage value, user agents should calculate the final radius value based on the associated object's width and height. The radius should be the smaller value of the two.
            * poly: x1, y1, x2, y2, ..., xN, yN. The first x and y coordinate pair and the last should be the same to close the polygon. When these coordinate values are not the same, user agents should infer an additional coordinate pair to close the polygon.
            
            Coordinates are relative to the top, left corner of the object. All values are lengths. All values are separated by commas
	 */
	 private String  _coords = null;

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
	 * shape
	 * default|rect|circle|poly [CI]
            This attribute specifies the shape of a region. Possible values:
            
            * default: Specifies the entire region.
            * rect: Define a rectangular region.
            * circle: Define a circular region.
            * poly: Define a polygonal region.
	 */
	 private String  _shape = null;

   	/*
	 * target
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 */
	 private String  _target = null;

   	/*
	 * charset
	 * The character encoding of a resource designated by this hyperlink
	 */
	 private String  _charset = null;

  	/*
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 private String  _styleClass = null;

  	/*
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	 private String  _accesskey = null;

  	/*
	 * outcome
	 * ${prop.xmlEncodedDescription}
	 */
	 private String  _outcome = null;

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
	 * onblur
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 */
	 private String  _onblur = null;

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
	 * hreflang
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 */
	 private String  _hreflang = null;

  	/*
	 * type
	 * The content type of the resource designated by this hyperlink
	 */
	 private String  _type = null;

  	/*
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	 private String  _onclick = null;

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
	 * onfocus
	 * JavaScript code. The onfocus event occurs when an element gets focus
	 */
	 private String  _onfocus = null;

  // Setters
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
	 * rev
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 */
	/**
	 * A reverse link from the anchor specified by this hyperlink to the current document. The value of this attribute is a space-separated list of link types
	 * Setter for rev
	 * @param rev - new value
	 */
	 public void setRev( String  __rev ){
		this._rev = __rev;
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
	 * actionExpression
	 * actionExpression
	 */
	/**
	 * actionExpression
	 * Setter for actionExpression
	 * @param actionExpression - new value
	 */
	 public void setActionExpression( String  __actionExpression ){
		this._actionExpression = __actionExpression;
     }
	 
   	/*
	 * disabled
	 * disabled
	 */
	/**
	 * disabled
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( String  __disabled ){
		this._disabled = __disabled;
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
	 * taskInstance
	 * Specify the task to operate on (e.g. for @StartTask)
	 */
	/**
	 * Specify the task to operate on (e.g. for @StartTask)
	 * Setter for taskInstance
	 * @param taskInstance - new value
	 */
	 public void setTaskInstance( String  __taskInstance ){
		this._taskInstance = __taskInstance;
     }
	 
   	/*
	 * rel
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 */
	/**
	 * The relationship from the current document to the anchor specified by this hyperlink. The value of this attribute is a space-separated list of link types
	 * Setter for rel
	 * @param rel - new value
	 */
	 public void setRel( String  __rel ){
		this._rel = __rel;
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
	 
   	/*
	 * coords
	 * This attribute specifies the position and shape on the screen. The number and order of values depends on the shape being defined. Possible combinations:
            
            * rect: left-x, top-y, right-x, bottom-y.
            * circle: center-x, center-y, radius. Note. When the radius value is percentage value, user agents should calculate the final radius value based on the associated object's width and height. The radius should be the smaller value of the two.
            * poly: x1, y1, x2, y2, ..., xN, yN. The first x and y coordinate pair and the last should be the same to close the polygon. When these coordinate values are not the same, user agents should infer an additional coordinate pair to close the polygon.
            
            Coordinates are relative to the top, left corner of the object. All values are lengths. All values are separated by commas
	 */
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
	 * shape
	 * default|rect|circle|poly [CI]
            This attribute specifies the shape of a region. Possible values:
            
            * default: Specifies the entire region.
            * rect: Define a rectangular region.
            * circle: Define a circular region.
            * poly: Define a polygonal region.
	 */
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
	 
     	/*
	 * target
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 */
	/**
	 * This attribute specifies the name of a frame where a document is to be opened.
            
            By assigning a name to a frame via the name attribute, authors can refer to it as the "target" of links defined by other elements
	 * Setter for target
	 * @param target - new value
	 */
	 public void setTarget( String  __target ){
		this._target = __target;
     }
	 
     	/*
	 * charset
	 * The character encoding of a resource designated by this hyperlink
	 */
	/**
	 * The character encoding of a resource designated by this hyperlink
	 * Setter for charset
	 * @param charset - new value
	 */
	 public void setCharset( String  __charset ){
		this._charset = __charset;
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
	 * outcome
	 * ${prop.xmlEncodedDescription}
	 */
	/**
	 * $prop.description
	 * Setter for outcome
	 * @param outcome - new value
	 */
	 public void setOutcome( String  __outcome ){
		this._outcome = __outcome;
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
	 * onblur
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 */
	/**
	 * JavaScript code. The onblur event occurs when an element loses focus either by the pointing device or by tabbing navigation. It may be used with the same elements as onfocus
	 * Setter for onblur
	 * @param onblur - new value
	 */
	 public void setOnblur( String  __onblur ){
		this._onblur = __onblur;
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
	 * hreflang
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 */
	/**
	 * Base language of a resource specified with the href attribute; hreflang may only be used with href
	 * Setter for hreflang
	 * @param hreflang - new value
	 */
	 public void setHreflang( String  __hreflang ){
		this._hreflang = __hreflang;
     }
	 
   	/*
	 * type
	 * The content type of the resource designated by this hyperlink
	 */
	/**
	 * The content type of the resource designated by this hyperlink
	 * Setter for type
	 * @param type - new value
	 */
	 public void setType( String  __type ){
		this._type = __type;
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
	 * onfocus
	 * JavaScript code. The onfocus event occurs when an element gets focus
	 */
	/**
	 * JavaScript code. The onfocus event occurs when an element gets focus
	 * Setter for onfocus
	 * @param onfocus - new value
	 */
	 public void setOnfocus( String  __onfocus ){
		this._onfocus = __onfocus;
     }
	 
    // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
  	    this._dir = null;
   	    this._rev = null;
  	    this._pageflow = null;
  	    this._style = null;
  	    this._propagation = null;
  	    this._onmouseover = null;
  	    this._onkeyup = null;
  	    this._tabindex = null;
   	    this._lang = null;
  	    this._actionExpression = null;
  	    this._disabled = null;
   	    this._onmouseout = null;
  	    this._taskInstance = null;
  	    this._rel = null;
  	    this._onmouseup = null;
   	    this._onmousemove = null;
  	    this._coords = null;
  	    this._actionListener = null;
  	    this._title = null;
   	    this._shape = null;
   	    this._target = null;
   	    this._charset = null;
  	    this._styleClass = null;
  	    this._accesskey = null;
  	    this._outcome = null;
  	    this._fragment = null;
  	    this._onkeypress = null;
  	    this._ondblclick = null;
   	    this._onblur = null;
  	    this._view = null;
   	    this._action = null;
  	    this._hreflang = null;
  	    this._type = null;
  	    this._onclick = null;
  	    this._onkeydown = null;
  	    this._onmousedown = null;
  	    this._immediate = null;
  	    this._onfocus = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
  		 		 			setStringProperty(component, "dir",this._dir);
		    		 		 			setStringProperty(component, "rev",this._rev);
		   		 		 			setStringProperty(component, "pageflow",this._pageflow);
		   		 		 			setStringProperty(component, "style",this._style);
		   		 		 			setStringProperty(component, "propagation",this._propagation);
		   		 		 			setStringProperty(component, "onmouseover",this._onmouseover);
		   		 		 			setStringProperty(component, "onkeyup",this._onkeyup);
		   		 		 			setStringProperty(component, "tabindex",this._tabindex);
		    		 		 			setStringProperty(component, "lang",this._lang);
		   		 		 			// TODO - setup properties for other cases.
			// name actionExpression with type javax.el.MethodExpression
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "disabled",this._disabled); 
		    		 		 			setStringProperty(component, "onmouseout",this._onmouseout);
		   		 		 			setValueBinding(component, "taskInstance",this._taskInstance);
		   		 		 			setStringProperty(component, "rel",this._rel);
		   		 		 			setStringProperty(component, "onmouseup",this._onmouseup);
		    		 		 			setStringProperty(component, "onmousemove",this._onmousemove);
		   		 		 			setStringProperty(component, "coords",this._coords);
		   		 		 			setActionListenerProperty(component, this._actionListener);
		   		 		 			setStringProperty(component, "title",this._title);
		    		 		 			setStringProperty(component, "shape",this._shape);
		    		 		 			setStringProperty(component, "target",this._target);
		    		 		 			setStringProperty(component, "charset",this._charset);
		   		 		 			setStringProperty(component, "styleClass",this._styleClass);
		   		 		 			setStringProperty(component, "accesskey",this._accesskey);
		   		 		 			setStringProperty(component, "outcome",this._outcome);
		   		 		 			setStringProperty(component, "fragment",this._fragment);
		   		 		 			setStringProperty(component, "onkeypress",this._onkeypress);
		   		 		 			setStringProperty(component, "ondblclick",this._ondblclick);
		    		 		 			setStringProperty(component, "onblur",this._onblur);
		   		 		 			setStringProperty(component, "view",this._view);
		    		 		 			setActionProperty(component, this._action);
		   		 		 			setStringProperty(component, "hreflang",this._hreflang);
		   		 		 			setStringProperty(component, "type",this._type);
		   		 		 			setStringProperty(component, "onclick",this._onclick);
		   		 		 			setStringProperty(component, "onkeydown",this._onkeydown);
		   		 		 			setStringProperty(component, "onmousedown",this._onmousedown);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "immediate",this._immediate); 
		   		 		 			setStringProperty(component, "onfocus",this._onfocus);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.Link";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.LinkRenderer";
			}

}
