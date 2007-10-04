/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import javax.faces.webapp.ConverterELTag ;
import javax.faces.component.UIComponent;
import javax.faces.convert.Converter;
import javax.faces.webapp.UIComponentTag;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import org.apache.commons.beanutils.ConvertUtils;
import javax.servlet.jsp.JspException;
import org.jboss.seam.ui.converter.EnumConverter;

public class ConvertEnumTag extends javax.faces.webapp.ConverterELTag 
{

// Fields
// Setters

  protected Converter createConverter() throws JspException 
  {
    EnumConverter converter = (EnumConverter) FacesContext.getCurrentInstance().getApplication().createConverter("org.jboss.seam.ui.EnumConverter");
    _setProperties(converter);
    return converter;
  }

  // Support method to wire in attributes
  private void _setProperties(EnumConverter converter) throws JspException 
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
  }

}
