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
	 * localValueSet
	 * localValueSet
	 */
	 private String  _localValueSet = null;

   	/*
	 * fileSize
	 * this value binding receives the file size (optional).
	 */
	 private String  _fileSize = null;

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
	 * accept
	 * a comma-separated list of content types to accept, may not be supported by the browser. E.g. "images/png,images/jpg", "images/*".
	 */
	 private String  _accept = null;

   	/*
	 * localFileName
	 * localFileName
	 */
	 private String  _localFileName = null;

   	/*
	 * required
	 * required
	 */
	 private String  _required = null;

  	/*
	 * validatorMessage
	 * validatorMessage
	 */
	 private String  _validatorMessage = null;

  	/*
	 * valid
	 * valid
	 */
	 private String  _valid = null;

  	/*
	 * valueChangeListener
	 * valueChangeListener
	 */
	 private String  _valueChangeListener = null;

  	/*
	 * contentType
	 * the property to receive the contentType
	 */
	 private String  _contentType = null;

  	/*
	 * localInputStream
	 * localInputStream
	 */
	 private String  _localInputStream = null;

  	/*
	 * validator
	 * validator
	 */
	 private String  _validator = null;

  	/*
	 * fileName
	 * this value binding receives the filename (optional).
	 */
	 private String  _fileName = null;

  	/*
	 * converterMessage
	 * converterMessage
	 */
	 private String  _converterMessage = null;

   	/*
	 * immediate
	 * immediate
	 */
	 private String  _immediate = null;

  	/*
	 * data
	 * this value binding receives the file's content type (optional).
	 */
	 private String  _data = null;

   // Setters
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
	 * required
	 * required
	 */
	/**
	 * required
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
	 * valueChangeListener
	 * valueChangeListener
	 */
	/**
	 * valueChangeListener
	 * Setter for valueChangeListener
	 * @param valueChangeListener - new value
	 */
	 public void setValueChangeListener( String  __valueChangeListener ){
		this._valueChangeListener = __valueChangeListener;
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
	 * validator
	 */
	/**
	 * validator
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
	 * immediate
	 */
	/**
	 * immediate
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
	 
      // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
  	    this._localValueSet = null;
   	    this._fileSize = null;
    	    this._styleClass = null;
  	    this._localContentType = null;
  	    this._requiredMessage = null;
  	    this._localFileSize = null;
  	    this._style = null;
  	    this._accept = null;
   	    this._localFileName = null;
   	    this._required = null;
  	    this._validatorMessage = null;
  	    this._valid = null;
  	    this._valueChangeListener = null;
  	    this._contentType = null;
  	    this._localInputStream = null;
  	    this._validator = null;
  	    this._fileName = null;
  	    this._converterMessage = null;
   	    this._immediate = null;
  	    this._data = null;
   	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
  		 		 			// Simple type - boolean
			setBooleanProperty(component, "localValueSet",this._localValueSet); 
		    		 		 			setIntegerProperty(component, "fileSize",this._fileSize); 
		     		 		 			setStringProperty(component, "styleClass",this._styleClass);
		   		 		 			setStringProperty(component, "localContentType",this._localContentType);
		   		 		 			setStringProperty(component, "requiredMessage",this._requiredMessage);
		   		 		 			setIntegerProperty(component, "localFileSize",this._localFileSize); 
		   		 		 			setStringProperty(component, "style",this._style);
		   		 		 			setStringProperty(component, "accept",this._accept);
		    		 		 			setStringProperty(component, "localFileName",this._localFileName);
		    		 		 			// Simple type - boolean
			setBooleanProperty(component, "required",this._required); 
		   		 		 			setStringProperty(component, "validatorMessage",this._validatorMessage);
		   		 		 			// Simple type - boolean
			setBooleanProperty(component, "valid",this._valid); 
		   		 		 			setValueChangedListenerProperty(component, this._valueChangeListener);
		   		 		 			setStringProperty(component, "contentType",this._contentType);
		   		 		 			// TODO - setup properties for other cases.
			// name localInputStream with type java.io.InputStream
		   		 		 			setValidatorProperty(component, this._validator);
		   		 		 			setStringProperty(component, "fileName",this._fileName);
		   		 		 			setStringProperty(component, "converterMessage",this._converterMessage);
		    		 		 			// Simple type - boolean
			setBooleanProperty(component, "immediate",this._immediate); 
		   		 		 			// TODO - handle object
			setStringProperty(component, "data",this._data);
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
