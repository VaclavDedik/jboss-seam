/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.Double ;
import java.lang.Integer ;
import org.jboss.seam.ui.graphicImage.UITransformImageSize ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.graphicImage.TransformImageSize
 * Component-Family org.jboss.seam.ui.graphicImage.TransformImageSize
  	 * 
 */
 public class HtmlTransformImageSize extends org.jboss.seam.ui.graphicImage.UITransformImageSize {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.graphicImage.TransformImageSize";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlTransformImageSize (){
  	  }

// Component properties fields
 	/**
	 * factor
	 * 
	 */
	 	 private Double  _factor = null; /* Default is null*/
	 	  	/**
	 * width
	 * 
	 */
	 	 private Integer  _width = null; /* Default is null*/
	 	  	/**
	 * height
	 * 
	 */
	 	 private Integer  _height = null; /* Default is null*/
	 	      	/**
	 * maintainRatio
	 * 
	 */
	 	 private boolean  _maintainRatio = false;		
	/**
	 * Flag indicated what maintainRatio is set.
	 */
	 private boolean _maintainRatioSet = false;	
	 	 
// Getters-setters
    /**
	 * 
	 * Setter for factor
	 * @param factor - new value
	 */
	 public void setFactor( Double  __factor ){
		this._factor = __factor;
	 	 }


   /**
	 * 
	 * Getter for factor
	 * @return factor value from local variable or value bindings
	 */
	 public Double getFactor(  ){
	         if (null != this._factor)
        {
            return this._factor;
        	    }
        ValueBinding vb = getValueBinding("factor");
        if (null != vb){
            return (Double)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * 
	 * Setter for width
	 * @param width - new value
	 */
	 public void setWidth( Integer  __width ){
		this._width = __width;
	 	 }


   /**
	 * 
	 * Getter for width
	 * @return width value from local variable or value bindings
	 */
	 public Integer getWidth(  ){
	         if (null != this._width)
        {
            return this._width;
        	    }
        ValueBinding vb = getValueBinding("width");
        if (null != vb){
            return (Integer)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	      /**
	 * 
	 * Setter for height
	 * @param height - new value
	 */
	 public void setHeight( Integer  __height ){
		this._height = __height;
	 	 }


   /**
	 * 
	 * Getter for height
	 * @return height value from local variable or value bindings
	 */
	 public Integer getHeight(  ){
	         if (null != this._height)
        {
            return this._height;
        	    }
        ValueBinding vb = getValueBinding("height");
        if (null != vb){
            return (Integer)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	          /**
	 * 
	 * Setter for maintainRatio
	 * @param maintainRatio - new value
	 */
	 public void setMaintainRatio( boolean  __maintainRatio ){
		this._maintainRatio = __maintainRatio;
	 		this._maintainRatioSet = true;
	 	 }


   /**
	 * 
	 * Getter for maintainRatio
	 * @return maintainRatio value from local variable or value bindings
	 */
	 public boolean isMaintainRatio(  ){
	 		 if(this._maintainRatioSet){
			return this._maintainRatio;
		 }
    	ValueBinding vb = getValueBinding("maintainRatio");
    	if (vb != null) {
    	    Boolean value = (Boolean) vb.getValue(getFacesContext());
    	    if (null == value) {
    			return this._maintainRatio;
    	    }
    	    return (value.booleanValue());
    	} else {
    	    return (this._maintainRatio);
    	}
	 	 }
	  
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.graphicImage.TransformImageSize";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[6];
        values[0] = super.saveState(context);
 	 	          values[1] = _factor;
	   	 	
  	 	          values[2] = _width;
	   	 	
  	 	          values[3] = _height;
	   	 	
      	        values[4] = new Boolean(_maintainRatio);
	   	   values[5] = Boolean.valueOf(_maintainRatioSet);	
	   	 	
 	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
 	 	          _factor = (Double)values[1] ;
	   	 	
  	 	          _width = (Integer)values[2] ;
	   	 	
  	 	          _height = (Integer)values[3] ;
	   	 	
      	        _maintainRatio = ((Boolean)values[4]).booleanValue();
	   	   _maintainRatioSet = ((Boolean)values[5]).booleanValue();	
	   	 	
 	
		
	}	
// Utilites

}