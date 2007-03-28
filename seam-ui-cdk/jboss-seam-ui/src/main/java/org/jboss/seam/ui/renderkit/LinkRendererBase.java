package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.context.FacesContext;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.ui.component.UILink;

public class LinkRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UILink.class;
   }
   
   public String getHref(FacesContext facesContext, UILink link) throws IOException
   {
      if (!link.isDisabled())
      {
         return link.getUrl();
      }
      return null;
   }

}
