package org.jboss.seam.ui;

import java.io.IOException;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.security.Authentication;

/**
 * Only renders the child tags if the authenticated user contains at least one
 * of the specified (comma separated) roles
 *
 * @author Shane Bryzak
 */
public class UISecure extends UIComponentBase
{
  public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UISecure";

  private String roles;

  @Override
  public String getFamily()
  {
     return "org.jboss.seam.ui.Secure";
  }

  @Override
  public void encodeBegin(FacesContext context)
      throws IOException
  {
    if (roles != null)
    {
      String[] parts = roles.split("[,]");

      Authentication auth = null;
      try
      {
        auth = Authentication.instance();

        for (int i = 0; i < parts.length; i++)
        {
          for (int j = 0; j < auth.getRoles().length; j++)
          {
            if (parts[i].equals(auth.getRoles()[j]))
              return;
          }
        }

        getChildren().clear();
      }
      catch (Exception ex)
      {
        // Hide the children anyway
        getChildren().clear();
      }
    }
  }

  public String getRoles()
  {
    return roles;
  }

  public void setRoles(String roles)
  {
    this.roles = roles;
  }

  @Override
  public void restoreState(FacesContext context, Object state) {
     Object[] values = (Object[]) state;
     super.restoreState(context, values[0]);
     roles = (String) values[1];
  }

  @Override
  public Object saveState(FacesContext context) {
     Object[] values = new Object[2];
     values[0] = super.saveState(context);
     values[1] = roles;
     return values;
   }
}
