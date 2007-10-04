/**
 * GENERATED FILE - DO NOT EDIT
 *
 */
package org.jboss.seam.ui.taglib;

import javax.faces.webapp.ValidatorELTag ;
import javax.faces.component.UIComponent;
import javax.faces.webapp.UIComponentTag;
import javax.faces.validator.Validator;
import org.apache.commons.beanutils.ConvertUtils;
import javax.faces.context.FacesContext;
import javax.servlet.jsp.JspException;
import javax.faces.el.ValueBinding;
import org.jboss.seam.ui.validator.FormattedTextValidator;

public class FormattedTextValidatorTag extends javax.faces.webapp.ValidatorELTag {

  // Fields
// Setters

  protected Validator createValidator() throws JspException
  {
    FormattedTextValidator validator = (FormattedTextValidator) FacesContext.getCurrentInstance().getApplication().createValidator("org.jboss.seam.ui.FormattedTextValidator");
    _setProperties(validator);
    return validator;
  }

  // Support method to wire in properties
  private void _setProperties(FormattedTextValidator validator) throws JspException 
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
  }
}
