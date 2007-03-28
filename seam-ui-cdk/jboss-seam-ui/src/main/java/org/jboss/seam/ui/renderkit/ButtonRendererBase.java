package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.ui.component.UIButton;

public class ButtonRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIButton.class;
   }
   

   public String getOnClick(FacesContext facesContext, UIButton button) throws IOException
   {
      String onclick = button.getOnclick();
      if (onclick == null)
      {
         onclick = "";
      }
      else if (onclick.length() > 0 && !onclick.endsWith(";"))
      {
         onclick += ";";
      }
      if (!button.isDisabled())
      {
         onclick += "location.href='" + button.getUrl() + "'";
      }
      return onclick;
   }

}
