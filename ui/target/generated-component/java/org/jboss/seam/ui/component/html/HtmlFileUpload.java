/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import java.lang.Integer ;
import java.lang.Object ;
import org.jboss.seam.ui.component.UIFileUpload ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.FileUpload
 * Component-Family org.jboss.seam.ui.FileUpload
  	 * Renderer-Type org.jboss.seam.ui.FileUploadRenderer
  	 * Renders a file upload control. This control must be used within a form with an encoding type of multipart/form-data, i.e:

&amp;lt;h:form enctype="multipart/form-data"&amp;gt;
                                    
For multipart requests, the Seam Multipart servlet filter must also be configured in web.xml:                          
    
&amp;lt;filter&amp;gt;
  &amp;lt;filter-name&amp;gt;Seam Filter&amp;lt;/filter-name&amp;gt;
  &amp;lt;filter-class&amp;gt;org.jboss.seam.servlet.SeamFilter&amp;lt;/filter-class&amp;gt;
&amp;lt;/filter&amp;gt;

&amp;lt;filter-mapping&amp;gt;
  &amp;lt;filter-name&amp;gt;Seam Filter&amp;lt;/filter-name&amp;gt;
  &amp;lt;url-pattern&amp;gt;/*&amp;lt;/url-pattern&amp;gt;
&amp;lt;/filter-mapping&amp;gt; 
    
The following configuration options for multipart requests may be configured in components.xml:

* createTempFiles - if this option is set to true, uploaded files are streamed to a temporary file instead of in memory.
* maxRequestSize - the maximum size of a file upload request, in bytes. 

Here's an example:
          
&amp;lt;component class="org.jboss.seam.servlet.MultipartConfig"&amp;gt;
    &amp;lt;property name="createTempFiles"&amp;gt;true&amp;lt;/property&amp;gt;
    &amp;lt;property name="maxRequestSize"&amp;gt;1000000&amp;lt;/property&amp;gt;
&amp;lt;/component&amp;gt;
 */
 public class HtmlFileUpload extends org.jboss.seam.ui.component.UIFileUpload {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.FileUpload";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlFileUpload (){
  	  	setRendererType("org.jboss.seam.ui.FileUploadRenderer");
  	  }

// Component properties fields
   	/**
	 * fileSize
	 * this value binding receives the file size (optional).
	 */
	 	 private Integer  _fileSize = null; /* Default is null*/
	 	   	/**
	 * onchange
	 * HTML: script expression; the element value was changed
	 */
	 	 private String  _onchange = null; /* Default is null*/
	 	    	/**
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 	 private String  _style = null; /* Default is null*/
	 	  	/**
	 * size
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 */
	 	 private int  _size = Integer.MIN_VALUE;		
	/**
	 * Flag indicated what size is set.
	 */
	 private boolean _sizeSet = false;	
	 	  	/**
	 * accept
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 */
	 	 private String  _accept = null; /* Default is null*/
	 	  	/**
	 * onmouseover
	 * HTML: a script expression; a pointer is moved onto
	 */
	 	 private String  _onmouseover = null; /* Default is null*/
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
	 * maxlength
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 */
	 	 private int  _maxlength = Integer.MIN_VALUE;		
	/**
	 * Flag indicated what maxlength is set.
	 */
	 private boolean _maxlengthSet = false;	
	 	   	/**
	 * disabled
	 * When set for a form control, this boolean attribute disables the control for user input
	 */
	 	 private boolean  _disabled = false;		
	/**
	 * Flag indicated what disabled is set.
	 */
	 private boolean _disabledSet = false;	
	 	   	/**
	 * alt
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 */
	 	 private String  _alt = null; /* Default is null*/
	 	  	/**
	 * onmouseout
	 * HTML: a script expression; a pointer is moved away
	 */
	 	 private String  _onmouseout = null; /* Default is null*/
	 	    	/**
	 * fileName
	 * this value binding receives the filename (optional).
	 */
	 	 private String  _fileName = null; /* Default is null*/
	 	   	/**
	 * onselect
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 */
	 	 private String  _onselect = null; /* Default is null*/
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
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 	 private String  _styleClass = null; /* Default is null*/
	 	   	/**
	 * accesskey
	 * This attribute assigns an access key to an element. An access key is a single character from the document character set. Note: Authors should consider the input method of the expected reader when specifying an accesskey
	 */
	 	 private String  _accesskey = null; /* Default is null*/
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
	 * align
	 * left|center|right|justify [CI]
            Deprecated. This attribute specifies the horizontal alignment of its element with respect to the surrounding context. Possible values:
            
            * left: text lines are rendered flush left.
            * center: text lines are centered.
            * right: text lines are rendered flush right.
            * justify: text lines are justified to both margins.
            
            The default depends on the base text direction. For left to right text, the default is align=left, while for right to left text, the default is align=right
	 */
	 	 private String  _align = null; /* Default is null*/
	 	  	/**
	 * onblur
	 * HTML: script expression; the element lost the focus
	 */
	 	 private String  _onblur = null; /* Default is null*/
	 	     	/**
	 * onclick
	 * HTML: a script expression; a pointer button is clicked
	 */
	 	 private String  _onclick = null; /* Default is null*/
	 	   	/**
	 * onkeydown
	 * HTML: a script expression; a key is pressed down
	 */
	 	 private String  _onkeydown = null; /* Default is null*/
	 	  	/**
	 * contentType
	 * the property to receive the contentType
	 */
	 	 private String  _contentType = null; /* Default is null*/
	 	  	/**
	 * onmousedown
	 * HTML: script expression; a pointer button is pressed down
	 */
	 	 private String  _onmousedown = null; /* Default is null*/
	 	    	/**
	 * data
	 * this value binding receives the file's content type (optional).
	 */
	 	 private Object  _data = null; /* Default is null*/
	 	  	/**
	 * onfocus
	 * HTML: script expression; the element got the focus
	 */
	 	 private String  _onfocus = null; /* Default is null*/
	 	  
// Getters-setters
      /**
	 * this value binding receives the file size (optional).
	 * Setter for fileSize
	 * @param fileSize - new value
	 */
	 public void setFileSize( Integer  __fileSize ){
		this._fileSize = __fileSize;
	 	 }


   /**
	 * this value binding receives the file size (optional).
	 * Getter for fileSize
	 * @return fileSize value from local variable or value bindings
	 */
	 public Integer getFileSize(  ){
	         if (null != this._fileSize)
        {
            return this._fileSize;
        	    }
        ValueBinding vb = getValueBinding("fileSize");
        if (null != vb){
            return (Integer)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * HTML: script expression; the element value was changed
	 * Setter for onchange
	 * @param onchange - new value
	 */
	 public void setOnchange( String  __onchange ){
		this._onchange = __onchange;
	 	 }


   /**
	 * HTML: script expression; the element value was changed
	 * Getter for onchange
	 * @return onchange value from local variable or value bindings
	 */
	 public String getOnchange(  ){
	         if (null != this._onchange)
        {
            return this._onchange;
        	    }
        ValueBinding vb = getValueBinding("onchange");
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
	      /**
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 * Setter for size
	 * @param size - new value
	 */
	 public void setSize( int  __size ){
		this._size = __size;
	 		this._sizeSet = true;
	 	 }


   /**
	 * This attribute tells the user agent the initial width of the control. The width is given in pixels except when type attribute has the value "text" or "password". In that case, its value refers to the (integer) number of characters
	 * Getter for size
	 * @return size value from local variable or value bindings
	 */
	 public int getSize(  ){
	 		 if(this._sizeSet){
			return this._size;
		 }
    	ValueBinding vb = getValueBinding("size");
    	if (vb != null) {
    	    Integer value = (Integer) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._size;
    	    }
    	    return (value.intValue());
    	} else {
    	    return (this._size);
    	}
	 	 }
	      /**
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 * Setter for accept
	 * @param accept - new value
	 */
	 public void setAccept( String  __accept ){
		this._accept = __accept;
	 	 }


   /**
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 * Getter for accept
	 * @return accept value from local variable or value bindings
	 */
	 public String getAccept(  ){
	         if (null != this._accept)
        {
            return this._accept;
        	    }
        ValueBinding vb = getValueBinding("accept");
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
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 * Setter for maxlength
	 * @param maxlength - new value
	 */
	 public void setMaxlength( int  __maxlength ){
		this._maxlength = __maxlength;
	 		this._maxlengthSet = true;
	 	 }


   /**
	 * When the type attribute has the value "text" or "password", this attribute specifies the maximum number of characters the user may enter. This number may exceed the specified size, in which case the user agent should offer a scrolling mechanism. The default value for this attribute is an unlimited number
	 * Getter for maxlength
	 * @return maxlength value from local variable or value bindings
	 */
	 public int getMaxlength(  ){
	 		 if(this._maxlengthSet){
			return this._maxlength;
		 }
    	ValueBinding vb = getValueBinding("maxlength");
    	if (vb != null) {
    	    Integer value = (Integer) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._maxlength;
    	    }
    	    return (value.intValue());
    	} else {
    	    return (this._maxlength);
    	}
	 	 }
	       /**
	 * When set for a form control, this boolean attribute disables the control for user input
	 * Setter for disabled
	 * @param disabled - new value
	 */
	 public void setDisabled( boolean  __disabled ){
		this._disabled = __disabled;
	 		this._disabledSet = true;
	 	 }


   /**
	 * When set for a form control, this boolean attribute disables the control for user input
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
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 * Setter for alt
	 * @param alt - new value
	 */
	 public void setAlt( String  __alt ){
		this._alt = __alt;
	 	 }


   /**
	 * For a user agents that cannot display images, forms, or applets, this attribute specifies alternate text. The language of the alternate text is specified by the lang attribute
	 * Getter for alt
	 * @return alt value from local variable or value bindings
	 */
	 public String getAlt(  ){
	         if (null != this._alt)
        {
            return this._alt;
        	    }
        ValueBinding vb = getValueBinding("alt");
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
	 * this value binding receives the filename (optional).
	 * Setter for fileName
	 * @param fileName - new value
	 */
	 public void setFileName( String  __fileName ){
		this._fileName = __fileName;
	 	 }


   /**
	 * this value binding receives the filename (optional).
	 * Getter for fileName
	 * @return fileName value from local variable or value bindings
	 */
	 public String getFileName(  ){
	         if (null != this._fileName)
        {
            return this._fileName;
        	    }
        ValueBinding vb = getValueBinding("fileName");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	       /**
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 * Setter for onselect
	 * @param onselect - new value
	 */
	 public void setOnselect( String  __onselect ){
		this._onselect = __onselect;
	 	 }


   /**
	 * HTML: script expression; The onselect event occurs when a user selects some text in a text field. This attribute may be used with the INPUT and TEXTAREA elements
	 * Getter for onselect
	 * @return onselect value from local variable or value bindings
	 */
	 public String getOnselect(  ){
	         if (null != this._onselect)
        {
            return this._onselect;
        	    }
        ValueBinding vb = getValueBinding("onselect");
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


   /**
	 * left|center|right|justify [CI]
            Deprecated. This attribute specifies the horizontal alignment of its element with respect to the surrounding context. Possible values:
            
            * left: text lines are rendered flush left.
            * center: text lines are centered.
            * right: text lines are rendered flush right.
            * justify: text lines are justified to both margins.
            
            The default depends on the base text direction. For left to right text, the default is align=left, while for right to left text, the default is align=right
	 * Getter for align
	 * @return align value from local variable or value bindings
	 */
	 public String getAlign(  ){
	         if (null != this._align)
        {
            return this._align;
        	    }
        ValueBinding vb = getValueBinding("align");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: script expression; the element lost the focus
	 * Setter for onblur
	 * @param onblur - new value
	 */
	 public void setOnblur( String  __onblur ){
		this._onblur = __onblur;
	 	 }


   /**
	 * HTML: script expression; the element lost the focus
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
	 * the property to receive the contentType
	 * Setter for contentType
	 * @param contentType - new value
	 */
	 public void setContentType( String  __contentType ){
		this._contentType = __contentType;
	 	 }


   /**
	 * the property to receive the contentType
	 * Getter for contentType
	 * @return contentType value from local variable or value bindings
	 */
	 public String getContentType(  ){
	         if (null != this._contentType)
        {
            return this._contentType;
        	    }
        ValueBinding vb = getValueBinding("contentType");
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
	 * this value binding receives the file's content type (optional).
	 * Setter for data
	 * @param data - new value
	 */
	 public void setData( Object  __data ){
		this._data = __data;
	 	 }


   /**
	 * this value binding receives the file's content type (optional).
	 * Getter for data
	 * @return data value from local variable or value bindings
	 */
	 public Object getData(  ){
	         if (null != this._data)
        {
            return this._data;
        	    }
        ValueBinding vb = getValueBinding("data");
        if (null != vb){
            return (Object)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * HTML: script expression; the element got the focus
	 * Setter for onfocus
	 * @param onfocus - new value
	 */
	 public void setOnfocus( String  __onfocus ){
		this._onfocus = __onfocus;
	 	 }


   /**
	 * HTML: script expression; the element got the focus
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
	   
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[32];
        values[0] = super.saveState(context);
   	 	          values[1] = _fileSize;
	   	 	
   	 	          values[2] = _onchange;
	   	 	
    	 	          values[3] = _style;
	   	 	
  	        values[4] = new Integer(_size);
	   	   values[5] = Boolean.valueOf(_sizeSet);	
	   	 	
  	 	          values[6] = _accept;
	   	 	
  	 	          values[7] = _onmouseover;
	   	 	
  	 	          values[8] = _onkeyup;
	   	 	
   	 	          values[9] = _tabindex;
	   	 	
  	        values[10] = new Integer(_maxlength);
	   	   values[11] = Boolean.valueOf(_maxlengthSet);	
	   	 	
   	        values[12] = new Boolean(_disabled);
	   	   values[13] = Boolean.valueOf(_disabledSet);	
	   	 	
   	 	          values[14] = _alt;
	   	 	
  	 	          values[15] = _onmouseout;
	   	 	
    	 	          values[16] = _fileName;
	   	 	
   	 	          values[17] = _onselect;
	   	 	
  	 	          values[18] = _onmouseup;
	   	 	
   	 	          values[19] = _onmousemove;
	   	 	
    	 	          values[20] = _styleClass;
	   	 	
   	 	          values[21] = _accesskey;
	   	 	
  	 	          values[22] = _onkeypress;
	   	 	
  	 	          values[23] = _ondblclick;
	   	 	
  	 	          values[24] = _align;
	   	 	
  	 	          values[25] = _onblur;
	   	 	
     	 	          values[26] = _onclick;
	   	 	
   	 	          values[27] = _onkeydown;
	   	 	
  	 	          values[28] = _contentType;
	   	 	
  	 	          values[29] = _onmousedown;
	   	 	
    	 	          values[30] = _data;
	   	 	
  	 	          values[31] = _onfocus;
	   	 	
  	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
   	 	          _fileSize = (Integer)values[1] ;
	   	 	
   	 	          _onchange = (String)values[2] ;
	   	 	
    	 	          _style = (String)values[3] ;
	   	 	
  	        _size = ((Integer)values[4]).intValue();
	   	   _sizeSet = ((Boolean)values[5]).booleanValue();	
	   	 	
  	 	          _accept = (String)values[6] ;
	   	 	
  	 	          _onmouseover = (String)values[7] ;
	   	 	
  	 	          _onkeyup = (String)values[8] ;
	   	 	
   	 	          _tabindex = (String)values[9] ;
	   	 	
  	        _maxlength = ((Integer)values[10]).intValue();
	   	   _maxlengthSet = ((Boolean)values[11]).booleanValue();	
	   	 	
   	        _disabled = ((Boolean)values[12]).booleanValue();
	   	   _disabledSet = ((Boolean)values[13]).booleanValue();	
	   	 	
   	 	          _alt = (String)values[14] ;
	   	 	
  	 	          _onmouseout = (String)values[15] ;
	   	 	
    	 	          _fileName = (String)values[16] ;
	   	 	
   	 	          _onselect = (String)values[17] ;
	   	 	
  	 	          _onmouseup = (String)values[18] ;
	   	 	
   	 	          _onmousemove = (String)values[19] ;
	   	 	
    	 	          _styleClass = (String)values[20] ;
	   	 	
   	 	          _accesskey = (String)values[21] ;
	   	 	
  	 	          _onkeypress = (String)values[22] ;
	   	 	
  	 	          _ondblclick = (String)values[23] ;
	   	 	
  	 	          _align = (String)values[24] ;
	   	 	
  	 	          _onblur = (String)values[25] ;
	   	 	
     	 	          _onclick = (String)values[26] ;
	   	 	
   	 	          _onkeydown = (String)values[27] ;
	   	 	
  	 	          _contentType = (String)values[28] ;
	   	 	
  	 	          _onmousedown = (String)values[29] ;
	   	 	
    	 	          _data = (Object)values[30] ;
	   	 	
  	 	          _onfocus = (String)values[31] ;
	   	 	
  	
		
	}	
// Utilites

}