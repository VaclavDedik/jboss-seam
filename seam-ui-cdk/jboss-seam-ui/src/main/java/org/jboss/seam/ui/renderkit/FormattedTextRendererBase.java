package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.ui.component.UIFormattedText;

public class FormattedTextRendererBase extends org.ajax4jsf.framework.renderer.ComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIFormattedText.class;
   }
   
   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
     writer.write(((UIFormattedText) component).getFormattedText());
   }
}
