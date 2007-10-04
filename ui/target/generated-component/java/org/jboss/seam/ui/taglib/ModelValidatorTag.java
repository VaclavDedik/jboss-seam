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
import org.jboss.seam.ui.validator.ModelValidator;

public class ModelValidatorTag extends javax.faces.webapp.ValidatorELTag {

  // Fields
// Setters

  protected Validator createValidator() throws JspException
  {
    ModelValidator validator = (ModelValidator) FacesContext.getCurrentInstance().getApplication().createValidator("org.jboss.seam.ui.ModelValidator");
    _setProperties(validator);
    return validator;
  }

  // Support method to wire in properties
  private void _setProperties(ModelValidator validator) throws JspException 
  {
    FacesContext facesContext = FacesContext.getCurrentInstance();
  }
}
