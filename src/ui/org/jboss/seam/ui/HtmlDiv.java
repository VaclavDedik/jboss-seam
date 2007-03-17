package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.context.ResponseWriter;

public class HtmlDiv extends UIStyleDecoration
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlDiv";

   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.endElement("div");
   }
   
   @Override
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("div", this);
   }

}
