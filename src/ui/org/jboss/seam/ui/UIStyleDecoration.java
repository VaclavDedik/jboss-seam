package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.el.ValueBinding;

public abstract class UIStyleDecoration extends UIComponentBase
{
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.StyleDecoration";
   
   private String styleClass;
   private String style;

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
      style = (String) values[2];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[3];
      values[0] = super.saveState(context);
      values[1] = styleClass;
      values[2] = style;
      return values;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      if ( !isRendered() ) return;

      super.encodeBegin(context);
      ResponseWriter response = context.getResponseWriter();
      startElement(response);

      response.writeAttribute("id", getClientId(context), "id");
      
      ValueBinding classBinding = getValueBinding("styleClass");
      String styleClass = classBinding==null ? 
               this.styleClass : (String) classBinding.getValue(context);
      if (styleClass!=null) 
      {
         response.writeAttribute("class", styleClass, "styleClass");
      }

      ValueBinding styleBinding = getValueBinding("style");
      String style = styleBinding==null ? 
               this.style : (String) styleBinding.getValue(context);
      if (style!=null) 
      {
         response.writeAttribute("style", style, "style");
      }
   }

   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      if ( !isRendered() ) return;
      
      ResponseWriter response = context.getResponseWriter();
      endElement(response);
      response.flush();
      super.encodeEnd(context);
   }

   public abstract void startElement(ResponseWriter writer) throws IOException;
   public abstract void endElement(ResponseWriter writer) throws IOException;

   public String getStyle()
   {
      return style;
   }

   public void setStyle(String style)
   {
      this.style = style;
   }
   
}
