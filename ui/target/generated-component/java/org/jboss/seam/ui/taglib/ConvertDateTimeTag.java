/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import java.lang.String ;
import java.util.Locale ;
import java.util.TimeZone ;
import javax.faces.webapp.ConverterELTag ;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.webapp.UIComponentTag;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import org.apache.commons.beanutils.ConvertUtils;
import javax.servlet.jsp.JspException;
import org.jboss.seam.ui.converter.DateTimeConverter;

public class ConvertDateTimeTag extends javax.faces.webapp.ConverterELTag 
{

// Fields
  /*
   * locale
   * ${prop.xmlEncodedDescription}
   */
  private String  _locale = null;

  /*
   * timeZone
   * ${prop.xmlEncodedDescription}
   */
  private String  _timeZone = null;

  /*
   * type
   * ${prop.xmlEncodedDescription}
   */
  private String  _type = null;

  /*
   * dateStyle
   * ${prop.xmlEncodedDescription}
   */
  private String  _dateStyle = null;

  /*
   * pattern
   * ${prop.xmlEncodedDescription}
   */
  private String  _pattern = null;

  /*
   * timeStyle
   * ${prop.xmlEncodedDescription}
   */
  private String  _timeStyle = null;

// Setters
  /*
   * $prop.description
   * Setter for locale
   * @param locale - new value
   */
  public void setLocale(String  __locale) 
  {
    this._locale = __locale;
  }
	 
  /*
   * $prop.description
   * Setter for timeZone
   * @param timeZone - new value
   */
  public void setTimeZone(String  __timeZone) 
  {
    this._timeZone = __timeZone;
  }
	 
  /*
   * $prop.description
   * Setter for type
   * @param type - new value
   */
  public void setType(String  __type) 
  {
    this._type = __type;
  }
	 
  /*
   * $prop.description
   * Setter for dateStyle
   * @param dateStyle - new value
   */
  public void setDateStyle(String  __dateStyle) 
  {
    this._dateStyle = __dateStyle;
  }
	 
  /*
   * $prop.description
   * Setter for pattern
   * @param pattern - new value
   */
  public void setPattern(String  __pattern) 
  {
    this._pattern = __pattern;
  }
	 
  /*
   * $prop.description
   * Setter for timeStyle
   * @param timeStyle - new value
   */
  public void setTimeStyle(String  __timeStyle) 
  {
    this._timeStyle = __timeStyle;
  }
	 

  protected Converter createConverter() throws JspException 
  {
    DateTimeConverter converter = new DateTimeConverter();
    _setProperties(converter);
    return converter;
  }

  // Support method to wire in attributes
  private void _setProperties(DateTimeConverter converter) throws JspException 
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
    if (_locale != null) 
    {
      if (UIComponentTag.isValueReference(_locale)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_locale);
        converter.setLocale((Locale) vb.getValue(facesContext));
      }
    }
    if (_timeZone != null) 
    {
      if (UIComponentTag.isValueReference(_timeZone)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_timeZone);
        converter.setTimeZone((TimeZone) vb.getValue(facesContext));
      }
    }
    if (_type != null) 
    {
      if (UIComponentTag.isValueReference(_type)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_type);
        converter.setType((String) vb.getValue(facesContext));
      }
      else
      {
        converter.setType((String) ConvertUtils.convert(_type, String.class));
      }
    }
    if (_dateStyle != null) 
    {
      if (UIComponentTag.isValueReference(_dateStyle)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_dateStyle);
        converter.setDateStyle((String) vb.getValue(facesContext));
      }
      else
      {
        converter.setDateStyle((String) ConvertUtils.convert(_dateStyle, String.class));
      }
    }
    if (_pattern != null) 
    {
      if (UIComponentTag.isValueReference(_pattern)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_pattern);
        converter.setPattern((String) vb.getValue(facesContext));
      }
      else
      {
        converter.setPattern((String) ConvertUtils.convert(_pattern, String.class));
      }
    }
    if (_timeStyle != null) 
    {
      if (UIComponentTag.isValueReference(_timeStyle)) 
      {
        ValueBinding vb = facesContext.getApplication().createValueBinding(_timeStyle);
        converter.setTimeStyle((String) vb.getValue(facesContext));
      }
      else
      {
        converter.setTimeStyle((String) ConvertUtils.convert(_timeStyle, String.class));
      }
    }
  }

}
