package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.ui.component.UISeamCommandBase;

public abstract class LinkRendererBase extends SeamCommandRendererBase
{
   @Override
   public void writeStart(ResponseWriter writer, FacesContext facesContext, UISeamCommandBase seamCommand) throws IOException
   {
      writer.startElement("a", seamCommand);
   }
   
   @Override
   public void writeEnd(ResponseWriter writer, FacesContext facesContext, UISeamCommandBase seamCommand) throws IOException
   {
      Object label = seamCommand.getValue();
      if (label!=null) 
      {
         writer.writeText( label, null );
      }
   }
   
   @Override
   protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      context.getResponseWriter().endElement("a");
   }
   
}
