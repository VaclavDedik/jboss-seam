/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.convert.Converter ;
import java.io.InputStream ;
import java.lang.Integer ;
import java.lang.Object ;
import javax.faces.el.MethodBinding ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlFileUpload;

public class FileUploadTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
   	/*
	 * fileSize
	 * this value binding receives the file size (optional).
	 */
	 private String  _fileSize = null;

   	/*
	 * onchange
	 * HTML: script expression; the element value was changed
	 */
	 private String  _onchange = null;

  	/*
	 * requiredMessage
	 * requiredMessage
	 */
	 private String  _requiredMessage = null;

  	/*
	 * localFileSize
	 * localFileSize
	 */
	 private String  _localFileSize = null;

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
	 * accept
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 */
	 private String  _accept = null;

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
	 * localFileName
	 * localFileName
	 */
	 private String  _localFileName = null;

  	/*
	 * tabindex
	 * This attribute specifies the position of the current element in the tabbing order for the current document. This value must be a number between 0 and 32767. User agents should ignore leading zeros
	 */
	 private String  _tabindex = null;

  	/*
	 * maxlength
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 */
	 private String  _maxlength = null;

   	/*
	 * disabled
	 * When set for a form control, this boolean attribute disables the control for user input
	 */
	 private String  _disabled = null;

  	/*
	 * valid
	 * valid
	 */
	 private String  _valid = null;

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
	 * localInputStream
	 * localInputStream
	 */
	 private String  _localInputStream = null;

  	/*
	 * validator
	 * MethodBinding pointing at a method that is called during
            Process Validations phase of the request processing lifecycle,
            to validate the current value of this component
	 */
	 private String  _validator = null;

  	/*
	 * fileName
	 * this value binding receives the filename (optional).
	 */
	 private String  _fileName = null;

   	/*
	 * onselect
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 */
	 private String  _onselect = null;

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
	 * localValueSet
	 * localValueSet
	 */
	 private String  _localValueSet = null;

   	/*
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 private String  _styleClass = null;

  	/*
	 * localContentType
	 * localContentType
	 */
	 private String  _localContentType = null;

  	/*
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	 private String  _accesskey = null;

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
	 * align
	 * left|center|right|justify [CI]
            Deprecated. This attribute specifies the horizontal alignment of its element with respect to the surrounding context. Possible values:
            
            * left: text lines are rendered flush left.
            * center: text lines are centered.
            * right: text lines are rendered flush right.
            * justify: text lines are justified to both margins.
            
            The default depends on the base text direction. For left to right text, the default is align=left, while for right to left text, the default is align=right
	 */
	 private String  _align = null;

  	/*
	 * onblur
	 * HTML: script expression; the element lost the focus
	 */
	 private String  _onblur = null;

   	/*
	 * required
	 * If "true", this component is checked for non-empty input
	 */
	 private String  _required = null;

  	/*
	 * validatorMessage
	 * validatorMessage
	 */
	 private String  _validatorMessage = null;

  	/*
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	 private String  _onclick = null;

  	/*
	 * valueChangeListener
	 * Listener for value changes
	 */
	 private String  _valueChangeListener = null;

  	/*
	 * onkeydown
	 * HTML: a script expression; a key is pressed down
	 */
	 private String  _onkeydown = null;

  	/*
	 * contentType
	 * the property to receive the contentType
	 */
	 private String  _contentType = null;

  	/*
	 * onmousedown
	 * HTML: script expression; a pointer button is pressed down
	 */
	 private String  _onmousedown = null;

  	/*
	 * converterMessage
	 * converterMessage
	 */
	 private String  _converterMessage = null;

  	/*
	 * immediate
	 * A flag indicating that this component value must be converted
            and validated immediately (that is, during Apply Request Values
            phase), rather than waiting until a Process Validations phase
	 */
	 private String  _immediate = null;

  	/*
	 * data
	 * this value binding receives the file's content type (optional).
	 */
	 private String  _data = null;

  	/*
	 * onfocus
	 * HTML: script expression; the element got the focus
	 */
	 private String  _onfocus = null;

  // Setters
     	/*
	 * fileSize
	 * this value binding receives the file size (optional).
	 */
	/**
	 * this value binding receives the file size (optional).
	 * Setter for fileSize
	 * @param fileSize - new value
	 */
	 public void setFileSize( String  __fileSize ){
		this._fileSize = __fileSize;
     }
	 
     	/*
	 * onchange
	 * HTML: script expression; the element value was changed
	 */
	/**
	 * HTML: script expression; the element value was changed
	 * Setter for onchange
	 * @param onchange - new value
	 */
	 public void setOnchange( String  __onchange ){
		this._onchange = __onchange;
     }
	 
   	/*
	 * requiredMessage
	 * requiredMessage
	 */
	/**
	 * requiredMessage
	 * Setter for requiredMessage
	 * @param requiredMessage - new value
	 */
	 public void setRequiredMessage( String  __requiredMessage ){
		this._requiredMessage = __requiredMessage;
     }
	 
   	/*
	 * localFileSize
	 * localFileSize
	 */
	/**
	 * localFileSize
	 * Setter for localFileSize
	 * @param localFileSize - new value
	 */
	 public void setLocalFileSize( String  __localFileSize ){
		this._localFileSize = __localFileSize;
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
	 * accept
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 */
	/**
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 * Setter for accept
	 * @param accept - new value
	 */
	 public void setAccept( String  __accept ){
		this._accept = __accept;
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
	 * localFileName
	 * localFileName
	 */
	/**
	 * localFileName
	 * Setter for localFileName
	 * @param localFileName - new value
	 */
	 public void setLocalFileName( String  __localFileName ){
		this._localFileName = __localFileName;
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
	 * maxlength
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 */
	/**
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 * Setter for maxlength
	 * @param maxlength - new value
	 */
	 public void setMaxlength( String  __maxlength ){
		this._maxlength = __maxlength;
     }
	 
     	/*
	 * disabled
	 * When set for a form control, this boolean attribute disables the control for user input
	 */
	/**
	 * When set for a form control, this boolean attribute disables the control for user input
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( String  __disabled ){
		this._disabled = __disabled;
     }
	 
   	/*
	 * valid
	 * valid
	 */
	/**
	 * valid
	 * Setter for valid
	 * @param valid - new value
	 */
	 public void setValid( String  __valid ){
		this._valid = __valid;
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
	 * localInputStream
	 * localInputStream
	 */
	/**
	 * localInputStream
	 * Setter for localInputStream
	 * @param localInputStream - new value
	 */
	 public void setLocalInputStream( String  __localInputStream ){
		this._localInputStream = __localInputStream;
     }
	 
   	/*
	 * validator
	 * MethodBinding pointing at a method that is called during
            Process Validations phase of the request processing lifecycle,
            to validate the current value of this component
	 */
	/**
	 * MethodBinding pointing at a method that is called during
            Process Validations phase of the request processing lifecycle,
            to validate the current value of this component
	 * Setter for validator
	 * @param validator - new value
	 */
	 public void setValidator( String  __validator ){
		this._validator = __validator;
     }
	 
   	/*
	 * fileName
	 * this value binding receives the filename (optional).
	 */
	/**
	 * this value binding receives the filename (optional).
	 * Setter for fileName
	 * @param fileName - new value
	 */
	 public void setFileName( String  __fileName ){
		this._fileName = __fileName;
     }
	 
     	/*
	 * onselect
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 */
	/**
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 * Setter for onselect
	 * @param onselect - new value
	 */
	 public void setOnselect( String  __onselect ){
		this._onselect = __onselect;
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
	 * localValueSet
	 * localValueSet
	 */
	/**
	 * localValueSet
	 * Setter for localValueSet
	 * @param localValueSet - new value
	 */
	 public void setLocalValueSet( String  __localValueSet ){
		this._localValueSet = __localValueSet;
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
	 * localContentType
	 * localContentType
	 */
	/**
	 * localContentType
	 * Setter for localContentType
	 * @param localContentType - new value
	 */
	 public void setLocalContentType( String  __localContentType ){
		this._localContentType = __localContentType;
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
	 * align
	 * left|center|right|justify [CI]
            Deprecated. This attribute specifies the horizontal alignment of its element with respect to the surrounding context. Possible values:
            
            * left: text lines are rendered flush left.
            * center: text lines are centered.
            * right: text lines are rendered flush right.
            * justify: text lines are justified to both margins.
            
            The default depends on the base text direction. For left to right text, the default is align=left, while for right to left text, the default is align=right
	 */
	/**
	 * left|center|right|justify [CI]
            Deprecated. This attribute specifies the horizontal alignment of its element with respect to the surrounding context. Possible values:
            
            * left: text lines are rendered flush left.
            * center: text lines are centered.
            * right: text lines are rendered flush right.
            * justify: text lines are justified to both margins.
            
            The default depends on the base text direction. For left to right text, the default is align=left, while for right to left text, the default is align=right
	 * Setter for align
	 * @param align - new value
	 */
	 public void setAlign( String  __align ){
		this._align = __align;
     }
	 
   	/*
	 * onblur
	 * HTML: script expression; the element lost the focus
	 */
	/**
	 * HTML: script expression; the element lost the focus
	 * Setter for onblur
	 * @param onblur - new value
	 */
	 public void setOnblur( String  __onblur ){
		this._onblur = __onblur;
     }
	 
     	/*
	 * required
	 * If "true", this component is checked for non-empty input
	 */
	/**
	 * If "true", this component is checked for non-empty input
	 * Setter for required
	 * @param required - new value
	 */
	 public void setRequired( String  __required ){
		this._required = __required;
     }
	 
   	/*
	 * validatorMessage
	 * validatorMessage
	 */
	/**
	 * validatorMessage
	 * Setter for validatorMessage
	 * @param validatorMessage - new value
	 */
	 public void setValidatorMessage( String  __validatorMessage ){
		this._validatorMessage = __validatorMessage;
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
	 * valueChangeListener
	 * Listener for value changes
	 */
	/**
	 * Listener for value changes
	 * Setter for valueChangeListener
	 * @param valueChangeListener - new value
	 */
	 public void setValueChangeListener( String  __valueChangeListener ){
		this._valueChangeListener = __valueChangeListener;
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
	 * contentType
	 * the property to receive the contentType
	 */
	/**
	 * the property to receive the contentType
	 * Setter for contentType
	 * @param contentType - new value
	 */
	 public void setContentType( String  __contentType ){
		this._contentType = __contentType;
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
	 * converterMessage
	 * converterMessage
	 */
	/**
	 * converterMessage
	 * Setter for converterMessage
	 * @param converterMessage - new value
	 */
	 public void setConverterMessage( String  __converterMessage ){
		this._converterMessage = __converterMessage;
     }
	 
   	/*
	 * immediate
	 * A flag indicating that this component value must be converted
            and validated immediately (that is, during Apply Request Values
            phase), rather than waiting until a Process Validations phase
	 */
	/**
	 * A flag indicating that this component value must be converted
            and validated immediately (that is, during Apply Request Values
            phase), rather than waiting until a Process Validations phase
	 * Setter for immediate
	 * @param immediate - new value
	 */
	 public void setImmediate( String  __immediate ){
		this._immediate = __immediate;
     }
	 
   	/*
	 * data
	 * this value binding receives the file's content type (optional).
	 */
	/**
	 * this value binding receives the file's content type (optional).
	 * Setter for data
	 * @param data - new value
	 */
	 public void setData( String  __data ){
		this._data = __data;
     }
	 
   	/*
	 * onfocus
	 * HTML: script expression; the element got the focus
	 */
	/**
	 * HTML: script expression; the element got the focus
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
   	    this._fileSize = null;
   	    this._onchange = null;
  	    this._requiredMessage = null;
  	    this._localFileSize = null;
  	    this._style = null;
  	    this._size = null;
  	    this._accept = null;
  	    this._onmouseover = null;
  	    this._onkeyup = null;
  	    this._localFileName = null;
  	    this._tabindex = null;
  	    this._maxlength = null;
   	    this._disabled = null;
  	    this._valid = null;
  	    this._alt = null;
  	    this._onmouseout = null;
  	    this._localInputStream = null;
  	    this._validator = null;
  	    this._fileName = null;
   	    this._onselect = null;
  	    this._onmouseup = null;
   	    this._onmousemove = null;
  	    this._localValueSet = null;
   	    this._styleClass = null;
  	    this._localContentType = null;
  	    this._accesskey = null;
  	    this._onkeypress = null;
  	    this._ondblclick = null;
  	    this._align = null;
  	    this._onblur = null;
   	    this._required = null;
  	    this._validatorMessage = null;
  	    this._onclick = null;
  	    this._valueChangeListener = null;
  	    this._onkeydown = null;
  	    this._contentType = null;
  	    this._onmousedown = null;
  	    this._converterMessage = null;
  	    this._immediate = null;
  	    this._data = null;
  	    this._onfocus = null;
  	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
   		 		 			setValueBinding(component, "fileSize",this._fileSize);
		    		 		 			setStringProperty(component, "onchange",this._onchange);
		   		 		 			setStringProperty(component, "requiredMessage",this._requiredMessage);
		   		 		 			setIntegerProperty(component, "localFileSize",this._localFileSize); 
		   		 		 			setStringProperty(component, "style",this._style);
		   		 		 			// Simple type - int
			setIntegerProperty(component, "size",this._size); 
		   		 		 			setStringProperty(component, "accept",this._accept);
		   		 		 			setStringProperty(component, "onmouseover",this._onmouseover);
		   		 		 			setStringProperty(component, "onkeyup",this._onkeyup);
		   		 		 			setStringProperty(component, "localFileName",this._localFileName);
		   		 		 			setStringProperty(component, "tabindex",this._tabindex);
		   		 		 			// Simple type - int
			setIntegerProperty(component, "maxlength",this._maxlength); 
		    		 		 			// Simple type - boolean
			setBooleanProperty(component, "disabled",this._disabled); 
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "valid",this._valid); 
		   		 		 			setStringProperty(component, "alt",this._alt);
		   		 		 			setStringProperty(component, "onmouseout",this._onmouseout);
		   		 		 			// TODO - setup properties for other cases.
			// name localInputStream with type java.io.InputStream
		   		 		 			setValidatorProperty(component, this._validator);
		   		 		 			setValueBinding(component, "fileName",this._fileName);
		    		 		 			setStringProperty(component, "onselect",this._onselect);
		   		 		 			setStringProperty(component, "onmouseup",this._onmouseup);
		    		 		 			setStringProperty(component, "onmousemove",this._onmousemove);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "localValueSet",this._localValueSet); 
		    		 		 			setStringProperty(component, "styleClass",this._styleClass);
		   		 		 			setStringProperty(component, "localContentType",this._localContentType);
		   		 		 			setStringProperty(component, "accesskey",this._accesskey);
		   		 		 			setStringProperty(component, "onkeypress",this._onkeypress);
		   		 		 			setStringProperty(component, "ondblclick",this._ondblclick);
		   		 		 			setStringProperty(component, "align",this._align);
		   		 		 			setStringProperty(component, "onblur",this._onblur);
		    		 		 			// Simple type - boolean
			setBooleanProperty(component, "required",this._required); 
		   		 		 			setStringProperty(component, "validatorMessage",this._validatorMessage);
		   		 		 			setStringProperty(component, "onclick",this._onclick);
		   		 		 			setValueChangedListenerProperty(component, this._valueChangeListener);
		   		 		 			setStringProperty(component, "onkeydown",this._onkeydown);
		   		 		 			setValueBinding(component, "contentType",this._contentType);
		   		 		 			setStringProperty(component, "onmousedown",this._onmousedown);
		   		 		 			setStringProperty(component, "converterMessage",this._converterMessage);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "immediate",this._immediate); 
		   		 		 			setValueBinding(component, "data",this._data);
		   		 		 			setStringProperty(component, "onfocus",this._onfocus);
		      }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.FileUpload";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.FileUploadRenderer";
			}

}
