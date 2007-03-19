package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.ui.component.UIFragment;

public class FragmentRendererBase extends AjaxComponentRendererBase
{
   @Override
   protected Class getComponentClass()
   {
      return UIFragment.class;
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   @Override
   protected void doEncodeChildren(ResponseWriter writer, FacesContext facesContext, UIComponent component) throws IOException
   {
      renderChildren(facesContext, component);
   }

}
