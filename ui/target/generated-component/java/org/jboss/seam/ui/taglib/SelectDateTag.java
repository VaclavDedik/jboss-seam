/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import org.jboss.seam.ui.util.cdk.UIComponentTagBase ;
import java.lang.String ;
import javax.faces.component.UIComponent;
import org.jboss.seam.ui.component.html.HtmlSelectDate;

public class SelectDateTag extends org.jboss.seam.ui.util.cdk.UIComponentTagBase {

// Fields
 	/*
	 * endYear
	 * 
	 */
	 private String  _endYear = null;

  	/*
	 * startYear
	 * 
	 */
	 private String  _startYear = null;

  	/*
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 private String  _styleClass = null;

  	/*
	 * dateFormat
	 * 
	 */
	 private String  _dateFormat = null;

  	/*
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 private String  _style = null;

      	/*
	 * for
	 * 
	 */
	 private String  _for = null;

 // Setters
 	/*
	 * endYear
	 * 
	 */
	/**
	 * 
	 * Setter for endYear
	 * @param endYear - new value
	 */
	 public void setEndYear( String  __endYear ){
		this._endYear = __endYear;
     }
	 
   	/*
	 * startYear
	 * 
	 */
	/**
	 * 
	 * Setter for startYear
	 * @param startYear - new value
	 */
	 public void setStartYear( String  __startYear ){
		this._startYear = __startYear;
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
	 * dateFormat
	 * 
	 */
	/**
	 * 
	 * Setter for dateFormat
	 * @param dateFormat - new value
	 */
	 public void setDateFormat( String  __dateFormat ){
		this._dateFormat = __dateFormat;
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
	 * for
	 * 
	 */
	/**
	 * 
	 * Setter for for
	 * @param for - new value
	 */
	 public void setFor( String  __for ){
		this._for = __for;
     }
	 
  // Release

    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#release()
     */
    public void release()
    {
        // TODO Auto-generated method stub
        super.release();
 	    this._endYear = null;
  	    this._startYear = null;
  	    this._styleClass = null;
  	    this._dateFormat = null;
  	    this._style = null;
      	    this._for = null;
 	}
	
    /* (non-Javadoc)
     * @see org.ajax4jsf.components.taglib.html.HtmlCommandButtonTagBase#setProperties(javax.faces.component.UIComponent)
     */
    protected void setProperties(UIComponent component)
    {
        // TODO Auto-generated method stub
        super.setProperties(component);
	
 		 		 			// Simple type - int
			setIntegerProperty(component, "endYear",this._endYear); 
		   		 		 			// Simple type - int
			setIntegerProperty(component, "startYear",this._startYear); 
		   		 		 			setStringProperty(component, "styleClass",this._styleClass);
		   		 		 			setStringProperty(component, "dateFormat",this._dateFormat);
		   		 		 			setStringProperty(component, "style",this._style);
		       		 		 			setStringProperty(component, "for",this._for);
		     }
	
	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getComponentType()
	 */
	public String getComponentType() {
		// TODO Auto-generated method stub
		return "org.jboss.seam.ui.SelectDate";
	}

	/* (non-Javadoc)
	 * @see javax.faces.webapp.UIComponentTag#getRendererType()
	 */
	public String getRendererType() {
				return "org.jboss.seam.ui.SelectDateRenderer";
			}

}
