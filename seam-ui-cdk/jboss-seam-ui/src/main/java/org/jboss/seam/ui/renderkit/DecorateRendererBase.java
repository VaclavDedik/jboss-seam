package org.jboss.seam.ui.renderkit;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.ajax4jsf.framework.renderer.AjaxComponentRendererBase;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.ui.component.UIDecorate;
import org.jboss.seam.ui.util.Decoration;

public class DecorateRendererBase extends AjaxComponentRendererBase
{

   @Override
   protected Class getComponentClass()
   {
      return UIDecorate.class;
   }
   
   @Override
   protected void doEncodeBegin(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIDecorate decorate = (UIDecorate) component;
      
      Contexts.getEventContext().set("invalid", Decoration.isComponentHasFacesMessages(component, context));
      Contexts.getEventContext().set("required", Decoration.isComponentRequired(component, context));
      
      writer.startElement("span", component);
      writer.writeAttribute("id", component.getClientId(context), "id");
      
      boolean hasMessage = decorate.hasMessage();
      
      UIComponent aroundDecoration = decorate.getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = decorate.getDecoration("aroundInvalidField");
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(decorate);
         aroundDecoration.encodeBegin(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(decorate);
         aroundInvalidDecoration.encodeBegin(context);
      }
   }
   
   @Override
   protected void doEncodeEnd(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIDecorate decorate = (UIDecorate) component;
      boolean hasMessage = decorate.hasMessage();
      UIComponent aroundDecoration = decorate.getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = decorate.getDecoration("aroundInvalidField");
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(decorate);
         aroundDecoration.encodeEnd(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(decorate);
         aroundInvalidDecoration.encodeEnd(context);
      }
      context.getResponseWriter().endElement("span");

      Contexts.getEventContext().remove("invalid");
      Contexts.getEventContext().remove("required");
   }

   @Override
   protected void doEncodeChildren(ResponseWriter writer, FacesContext context, UIComponent component) throws IOException
   {
      UIDecorate decorate = (UIDecorate) component;
      
      boolean hasMessage = decorate.hasMessage();

      UIComponent beforeDecoration = decorate.getDecoration("beforeField");
      UIComponent beforeInvalidDecoration = decorate.getDecoration("beforeInvalidField");
      if ( beforeDecoration!=null && !hasMessage )
      {
         beforeDecoration.setParent(decorate);
         renderChild(context, beforeDecoration);
      }
      if ( beforeInvalidDecoration!=null && hasMessage )
      {
         beforeInvalidDecoration.setParent(decorate);
         renderChild(context, beforeInvalidDecoration);
      }
      
      renderChildren(context, decorate);
      
      UIComponent afterDecoration = decorate.getDecoration("afterField");
      UIComponent afterInvalidDecoration = decorate.getDecoration("afterInvalidField");
      if ( afterDecoration!=null  && !hasMessage )
      {
         afterDecoration.setParent(decorate);
         renderChild(context, afterDecoration);
      }
      if ( afterInvalidDecoration!=null && hasMessage )
      {
         afterInvalidDecoration.setParent(decorate);
         renderChild(context, afterInvalidDecoration);
      }

   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
}