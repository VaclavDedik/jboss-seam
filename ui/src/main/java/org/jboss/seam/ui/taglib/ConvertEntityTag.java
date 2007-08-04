/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.servlet.jsp.JspException;

public class ConvertEntityTag extends javax.faces.webapp.ConverterELTag 
{

// Fields
// Setters

  @Override
  protected Converter createConverter() throws JspException 
  {
    Converter converter = FacesContext.getCurrentInstance().getApplication().createConverter("org.jboss.seam.ui.EntityConverter");
    return converter;
  }

}
