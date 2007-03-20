package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.jboss.seam.ui.component.UIButton;
import org.jboss.seam.ui.component.UISeamCommandBase;

public abstract class ButtonRendererBase extends SeamCommandRendererBase
{
   @Override
   public void writeStart(ResponseWriter writer, FacesContext facesContext,
            UISeamCommandBase seamCommand) throws IOException
   {
      
      UIButton button = (UIButton) seamCommand;
      writer.startElement("input", seamCommand);

      if (button.getImage() == null) {
          writer.writeAttribute("type", "button", null);
      } else {
          writer.writeAttribute("type", "image", null);
          writer.writeAttribute("src", button.getImage(), null);
      }
      
      if ( seamCommand.disabled() ) writer.writeAttribute("disabled", true, "disabled");

   }

   @Override
   public void writeEnd(ResponseWriter writer, FacesContext facesContext,
            UISeamCommandBase seamCommand) throws IOException
   {
      Object label = seamCommand.getValue();
      if (label!=null) 
      {
         writer.writeAttribute("value", label, "label");
      }
   }
   
   @Override
   protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      writer.endElement("input");
   }
}
