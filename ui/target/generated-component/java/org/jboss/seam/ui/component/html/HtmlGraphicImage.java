/**
 * GENERATED FILE - DO NOT EDIT
 *
 */

package org.jboss.seam.ui.component.html;

import java.lang.String ;
import javax.faces.convert.Converter ;
import org.jboss.seam.ui.graphicImage.UIGraphicImage ;

import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;

/**
 * Component-Type org.jboss.seam.ui.graphicImage.GraphicImage
 * Component-Family org.jboss.seam.ui.graphicImage.GraphicImage
  	 * Renderer-Type org.jboss.seam.ui.GraphicImageRenderer
  	 * 
 */
 public class HtmlGraphicImage extends org.jboss.seam.ui.graphicImage.UIGraphicImage {

  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.graphicImage.GraphicImage";

  /**
   *  Constructor to init default renderers 
   */ 
  public HtmlGraphicImage (){
  	  	setRendererType("org.jboss.seam.ui.GraphicImageRenderer");
  	  }

// Component properties fields
                 	/**
	 * converter
	 * Id of Converter to be used or reference to a Converter
	 */
	 	 private Converter  _converter = null; /* Default is null*/
	 	         	/**
	 * fileName
	 * 
	 */
	 	 private String  _fileName = null; /* Default is null*/
	 	     
// Getters-setters
                    /**
	 * Id of Converter to be used or reference to a Converter
	 * Setter for converter
	 * @param converter - new value
	 */
	 public void setConverter( Converter  __converter ){
		this._converter = __converter;
	 	 }


   /**
	 * Id of Converter to be used or reference to a Converter
	 * Getter for converter
	 * @return converter value from local variable or value bindings
	 */
	 public Converter getConverter(  ){
	         if (null != this._converter)
        {
            return this._converter;
        	    }
        ValueBinding vb = getValueBinding("converter");
        if (null != vb){
            return (Converter)vb.getValue(getFacesContext());
		        } else {
            return null;
        }
	 	 }
	             /**
	 * 
	 * Setter for fileName
	 * @param fileName - new value
	 */
	 public void setFileName( String  __fileName ){
		this._fileName = __fileName;
	 	 }


   /**
	 * 
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
	      
// Component family.
	public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.graphicImage.GraphicImage";

	public String getFamily() {
		return COMPONENT_FAMILY;
	}

// Save state
// ----------------------------------------------------- StateHolder Methods


    public Object saveState(FacesContext context) {
        Object values[] = new Object[3];
        values[0] = super.saveState(context);
                 	 	          values[1] = saveAttachedState(context, _converter );		
	   	 	
         	 	          values[2] = _fileName;
	   	 	
     	  return values;
   }
   

    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;
        super.restoreState(context, values[0]);
                 	 	          _converter = (Converter)restoreAttachedState(context,values[1] );		
	   	 	
         	 	          _fileName = (String)values[2] ;
	   	 	
     	
		
	}	
// Utilites

}