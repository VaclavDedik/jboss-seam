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
import org.jboss.seam.ui.converter.EntityConverter;

public class ConvertEntityTag extends javax.faces.webapp.ConverterELTag 
{

// Fields
// Setters

  protected Converter createConverter() throws JspException 
  {
    EntityConverter converter = new EntityConverter();
    _setProperties(converter);
    return converter;
  }

  // Support method to wire in attributes
  private void _setProperties(EntityConverter converter) throws JspException 
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
  }

}
