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
  &amp;lt;filter-class&amp;gt;org.jboss.seam.web.SeamFilter&amp;lt;/filter-class&amp;gt;
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
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 	 private String  _styleClass = null; /* Default is null*/
	 	     	/**
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 	 private String  _style = null; /* Default is null*/
	 	  	/**
	 * accept
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 */
	 	 private String  _accept = null; /* Default is null*/
	 	         	/**
	 * contentType
	 * the property to receive the contentType
	 */
	 	 private String  _contentType = null; /* Default is null*/
	 	    	/**
	 * fileName
	 * this value binding receives the filename (optional).
	 */
	 	 private String  _fileName = null; /* Default is null*/
	 	     	/**
	 * data
	 * this value binding receives the file's content type (optional).
	 */
	 	 private Object  _data = null; /* Default is null*/
	 	   
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
	    
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.FileUpload";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[8];
        values[0] = super.saveState(context);
    	 	          values[1] = _fileSize;
	   	 	
    	 	          values[2] = _styleClass;
	   	 	
     	 	          values[3] = _style;
	   	 	
  	 	          values[4] = _accept;
	   	 	
         	 	          values[5] = _contentType;
	   	 	
    	 	          values[6] = _fileName;
	   	 	
     	 	          values[7] = _data;
	   	 	
   	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
    	 	          _fileSize = (Integer)values[1] ;
	   	 	
    	 	          _styleClass = (String)values[2] ;
	   	 	
     	 	          _style = (String)values[3] ;
	   	 	
  	 	          _accept = (String)values[4] ;
	   	 	
         	 	          _contentType = (String)values[5] ;
	   	 	
    	 	          _fileName = (String)values[6] ;
	   	 	
     	 	          _data = (Object)values[7] ;
	   	 	
   	
		
	}	
// Utilites

}