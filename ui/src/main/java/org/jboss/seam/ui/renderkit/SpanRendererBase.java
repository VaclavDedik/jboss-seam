package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

import org.jboss.seam.ui.component.UIStyle;

public class SpanRendererBase extends StyleRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIStyle.class;
   }

   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.endElement("span");
   }
   
   @Override
   public void startElement(ResponseWriter writer, UIStyle style) throws IOException
   {
      writer.startElement("span", style);
   }
   
}
