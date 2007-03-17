package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

public class HtmlSpan extends UIStyleDecoration
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlSpan";

   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.endElement("span");
   }
   
   @Override
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("span", this);
   }

}
