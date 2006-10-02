package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class UIStyleDecoration extends UIComponentBase
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.StyleDecoration";
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIStyleDecoration";
   
   private String styleClass;

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   public String getStyleClass()
   {
      return styleClass;
   }

   public void setStyleClass(String styleClass)
   {
      this.styleClass = styleClass;
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      styleClass = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = styleClass;
      return values;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      super.encodeBegin(context);
      ResponseWriter response = context.getResponseWriter();
      response.startElement("span", this);
      response.writeAttribute("class", styleClass, "styleClass");
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      ResponseWriter response = context.getResponseWriter();
      response.endElement("span");
      response.flush();
      super.encodeEnd(context);
   }

   
   
}
