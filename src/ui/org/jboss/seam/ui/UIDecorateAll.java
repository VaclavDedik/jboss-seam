package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class UIDecorateAll extends UIAbstractDecorate
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorateAll";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.DecorateAll";
   
   private String rowClass;
   private String rowErrorClass;
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("div", this);
      writer.writeAttribute("id", getClientId( getFacesContext() ), "id");
   }

   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("div", this);
      writer.writeAttribute("style", "clear: both;", null);
      writer.endElement("div");
      writer.endElement("div");
   }
   
   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();

      for (Object child : this.getChildren())
      {
         if (child instanceof UIComponent)
         {
            UIComponent component = (UIComponent) child;
            writer.startElement("div", this);
            String rowClasses = hasMessage(component, context) && rowErrorClass!=null ? 
                     rowClass + ' ' + rowErrorClass : rowClass;
            writer.writeAttribute("class", rowClasses, "rowClass");
            if (child instanceof UIDecorate)
            {
               renderContent(context, component);
            }
            else
            {
               renderChildAndDecorations(context, component);
            }
            writer.endElement("div");
         }
      }
   }
   
   @Override
   protected void renderContent(FacesContext context, UIComponent child) throws IOException
   {
      JSF.renderChild(context, child);
   }

   public String getRowClass()
   {
      return rowClass;
   }

   public void setRowClass(String rowClass)
   {
      this.rowClass = rowClass;
   }

   public String getRowErrorClass()
   {
      return rowErrorClass;
   }

   public void setRowErrorClass(String rowErrorClass)
   {
      this.rowErrorClass = rowErrorClass;
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] array = (Object[]) state;
      super.restoreState(context, array[0]);
      rowClass = (String) array[1];
      rowErrorClass = (String) array[2];
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[3];
      state[0] = super.saveState(context);
      state[1] = rowClass;
      state[2] = rowErrorClass;
      return state;
   }

   @Override
   protected void renderChild(FacesContext context, UIComponent child) throws IOException
   {
      if (child instanceof EditableValueHolder)
      {
         renderFieldAndDecorations(context, child);
      }
      else
      {
         renderContent(context, child);
      }
   }

}
