package org.jboss.seam.ui.tag;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.el.ValueBinding;
import javax.faces.webapp.UIComponentTag;

import org.jboss.seam.ui.UISecure;

/**
 * UISecure tag
 *
 * @author Shane Bryzak
 */
public class SecureTag extends UIComponentTag
{
  private String roles;

  @Override
  public String getComponentType()
  {
    return UISecure.COMPONENT_TYPE;
  }

  @Override
  public String getRendererType()
  {
    return null;
  }

  @Override
  protected void setProperties(UIComponent component)
  {
    super.setProperties(component);
    setStringProperty(component, "roles", roles);
  }

  public void setRoles(String roles)
  {
    this.roles = roles;
  }

  protected void setStringProperty(UIComponent component, String propName,

                                   String value)

  {
    setStringProperty(getFacesContext(), component, propName, value);
  }

  public static void setStringProperty(FacesContext context,
                                       UIComponent component,
                                       String propName, String value)
  {
    if (value != null)
    {
      if (isValueReference(value))
      {
        ValueBinding vb = context.getApplication().createValueBinding(value);
        component.setValueBinding(propName, vb);
      }
      else
      {
        // TODO: Warning if component has no such property (with reflection)
        component.getAttributes().put(propName, value);
      }
    }
  }
}
