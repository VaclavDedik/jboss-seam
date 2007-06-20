/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import org.jboss.seam.ui.component.UISelectDate ;
import java.lang.String ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.SelectDate
 * Component-Family org.jboss.seam.ui.SelectDate
  	 * Renderer-Type org.jboss.seam.ui.SelectDateRenderer
  	 * 
 */
 public class HtmlSelectDate extends org.jboss.seam.ui.component.UISelectDate {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.SelectDate";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlSelectDate (){
  	  	setRendererType("org.jboss.seam.ui.SelectDateRenderer");
  	  }

// Component properties fields
 	/**
	 * endYear
	 * 
	 */
	 	 private int  _endYear = -1;		
	/**
	 * Flag indicated what endYear is set.
	 */
	 private boolean _endYearSet = false;	
	 	  	/**
	 * startYear
	 * 
	 */
	 	 private int  _startYear = -1;		
	/**
	 * Flag indicated what startYear is set.
	 */
	 private boolean _startYearSet = false;	
	 	  	/**
	 * styleClass
	 * Corresponds to the HTML class attribute
	 */
	 	 private String  _styleClass = null; /* Default is null*/
	 	  	/**
	 * dateFormat
	 * 
	 */
	 	 private String  _dateFormat = null; /* Default is "MM/dd/yyyy"*/
	 	  	/**
	 * style
	 * CSS style(s) is/are to be applied when this component is rendered
	 */
	 	 private String  _style = null; /* Default is null*/
	 	      	/**
	 * for
	 * 
	 */
	 	 private String  _for = null; /* Default is null*/
	 	 
// Getters-setters
    /**
	 * 
	 * Setter for endYear
	 * @param endYear - new value
	 */
	 public void setEndYear( int  __endYear ){
		this._endYear = __endYear;
	 		this._endYearSet = true;
	 	 }


   /**
	 * 
	 * Getter for endYear
	 * @return endYear value from local variable or value bindings
	 */
	 public int getEndYear(  ){
	 		 if(this._endYearSet){
			return this._endYear;
		 }
    	ValueBinding vb = getValueBinding("endYear");
    	if (vb != null) {
    	    Integer value = (Integer) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._endYear;
    	    }
    	    return (value.intValue());
    	} else {
    	    return (this._endYear);
    	}
	 	 }
	      /**
	 * 
	 * Setter for startYear
	 * @param startYear - new value
	 */
	 public void setStartYear( int  __startYear ){
		this._startYear = __startYear;
	 		this._startYearSet = true;
	 	 }


   /**
	 * 
	 * Getter for startYear
	 * @return startYear value from local variable or value bindings
	 */
	 public int getStartYear(  ){
	 		 if(this._startYearSet){
			return this._startYear;
		 }
    	ValueBinding vb = getValueBinding("startYear");
    	if (vb != null) {
    	    Integer value = (Integer) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._startYear;
    	    }
    	    return (value.intValue());
    	} else {
    	    return (this._startYear);
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
	 * 
	 * Setter for dateFormat
	 * @param dateFormat - new value
	 */
	 public void setDateFormat( String  __dateFormat ){
		this._dateFormat = __dateFormat;
	 	 }


   /**
	 * 
	 * Getter for dateFormat
	 * @return dateFormat value from local variable or value bindings
	 */
	 public String getDateFormat(  ){
	         if (null != this._dateFormat)
        {
            return this._dateFormat;
        	    }
        ValueBinding vb = getValueBinding("dateFormat");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return "MM/dd/yyyy";
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
	 * 
	 * Setter for for
	 * @param for - new value
	 */
	 public void setFor( String  __for ){
		this._for = __for;
	 	 }


   /**
	 * 
	 * Getter for for
	 * @return for value from local variable or value bindings
	 */
	 public String getFor(  ){
	         if (null != this._for)
        {
            return this._for;
        	    }
        ValueBinding vb = getValueBinding("for");
        if (null != vb){
            return (String)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	  
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.SelectDate";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[9];
        values[0] = super.saveState(context);
 	        values[1] = new Integer(_endYear);
	   	   values[2] = Boolean.valueOf(_endYearSet);	
	   	 	
  	        values[3] = new Integer(_startYear);
	   	   values[4] = Boolean.valueOf(_startYearSet);	
	   	 	
  	 	          values[5] = _styleClass;
	   	 	
  	 	          values[6] = _dateFormat;
	   	 	
  	 	          values[7] = _style;
	   	 	
      	 	          values[8] = _for;
	   	 	
 	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	        _endYear = ((Integer)values[1]).intValue();
	   	   _endYearSet = ((Boolean)values[2]).booleanValue();	
	   	 	
  	        _startYear = ((Integer)values[3]).intValue();
	   	   _startYearSet = ((Boolean)values[4]).booleanValue();	
	   	 	
  	 	          _styleClass = (String)values[5] ;
	   	 	
  	 	          _dateFormat = (String)values[6] ;
	   	 	
  	 	          _style = (String)values[7] ;
	   	 	
      	 	          _for = (String)values[8] ;
	   	 	
 	
		
	}	
// Utilites

}