package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

import org.jboss.seam.contexts.Contexts;

public class UIDecorate extends UIComponentBase
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorate";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorate";
   
   private String forId;

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   protected static boolean hasMessage(UIComponent component, FacesContext context)
   {
      if ( !component.isRendered() ) return false;
      
      /*Iterator<FacesMessage> iter = context.getMessages( component.getClientId(context) );
      if ( iter.hasNext() )
      {
         return true;
      }*/
      
      if ( component instanceof EditableValueHolder )
      {
         if ( ! ( (EditableValueHolder) component ).isValid() ) return true;
      }

      for (Object child: component.getChildren())
      {
         if (child instanceof UIComponent)
         {
            boolean message = hasMessage( (UIComponent) child, context );
            if (message) return true;
         }
      }
      return false;
   }

   protected static boolean hasRequired(UIComponent component, FacesContext context)
   {
      if ( !component.isRendered() ) return false;
      
      if ( component instanceof EditableValueHolder )
      {
         if (  ( (EditableValueHolder) component ).isRequired() ) return true;
      }

      for (Object child: component.getChildren())
      {
         if (child instanceof UIComponent)
         {
            boolean required = hasRequired( (UIComponent) child, context );
            if (required) return true;
         }
      }
      return false;
   }

   private boolean hasMessage()
   {
      String clientId = getInputClientId();
      if (clientId==null)
      {
         return false;
      }
      else
      {
         return getFacesContext().getMessages(clientId).hasNext();
      }
   }

   public String getInputId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = getEditableValueHolder(this);
         return evh==null ? null : evh.getId();
      }
      else
      {
         return id;
      }
   }

   private String getInputClientId()
   {
      String id = getFor();
      if (id==null)
      {
         UIComponent evh = getEditableValueHolder(this);
         return evh==null ? null : evh.getClientId( getFacesContext() );
      }
      else
      {
         UIComponent component = findComponent(id);
         return component==null ? null : component.getClientId( getFacesContext() );
      }
   }

   /**
    * A depth-first search for an EditableValueHolder
    */
   private static UIComponent getEditableValueHolder(UIComponent component)
   {
      for (Object child: component.getChildren())
      {
         if (child instanceof EditableValueHolder)
         {
            UIComponent evh =(UIComponent) child;
            if ( evh.isRendered() )
            {
               return evh;
            }
         }
         else if (child instanceof UIComponent)
         {
            UIComponent evh = getEditableValueHolder( (UIComponent) child );
            if (evh!=null) return evh;
         }
      }
      return null;
   }

   @Override
   public boolean getRendersChildren()
   {
      return true;
   }

   public String getFor()
   {
      return forId;
   }

   public void setFor(String forId)
   {
      this.forId = forId;
   }

   private UIComponent getDecoration(String name)
   {
      return getDecoration(name, this);
   }
   
   private static UIComponent getDecoration(String name, UIComponent component)
   {
      UIComponent dec = component.getFacet(name);
      if (dec!=null) return dec;
      if ( component.getParent()==null ) return null;
      return getDecoration( name, component.getParent() );
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      super.encodeBegin(context);
      
      Contexts.getEventContext().set("invalid", hasMessage(this, context));
      Contexts.getEventContext().set("required", hasRequired(this, context));
 
      context.getResponseWriter().startElement("span", this);
      context.getResponseWriter().writeAttribute("id", getClientId(context), "id");
      boolean hasMessage = hasMessage();
      UIComponent aroundDecoration = getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = getDecoration("aroundInvalidField");
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(this);
         aroundDecoration.encodeBegin(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(this);
         aroundInvalidDecoration.encodeBegin(context);
      }
   }
   
   @Override
   public void encodeEnd(FacesContext context) throws IOException
   {
      boolean hasMessage = hasMessage();
      UIComponent aroundDecoration = getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = getDecoration("aroundInvalidField");
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(this);
         aroundDecoration.encodeEnd(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(this);
         aroundInvalidDecoration.encodeEnd(context);
      }
      context.getResponseWriter().endElement("span");

      Contexts.getEventContext().remove("invalid");
      Contexts.getEventContext().remove("required");

      super.encodeEnd(context);
   }

   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      boolean hasMessage = hasMessage();

      UIComponent beforeDecoration = getDecoration("beforeField");
      UIComponent beforeInvalidDecoration = getDecoration("beforeInvalidField");
      if ( beforeDecoration!=null && !hasMessage )
      {
         beforeDecoration.setParent(this);
         JSF.renderChild(facesContext, beforeDecoration);
      }
      if ( beforeInvalidDecoration!=null && hasMessage )
      {
         beforeInvalidDecoration.setParent(this);
         JSF.renderChild(facesContext, beforeInvalidDecoration);
      }
      
      JSF.renderChildren(facesContext, this);
      
      UIComponent afterDecoration = getDecoration("afterField");
      UIComponent afterInvalidDecoration = getDecoration("afterInvalidField");
      if ( afterDecoration!=null  && !hasMessage )
      {
         afterDecoration.setParent(this);
         JSF.renderChild(facesContext, afterDecoration);
      }
      if ( afterInvalidDecoration!=null && hasMessage )
      {
         afterInvalidDecoration.setParent(this);
         JSF.renderChild(facesContext, afterInvalidDecoration);
      }
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      forId = (String) values[1];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[2];
      values[0] = super.saveState(context);
      values[1] = forId;
      return values;
   }

}
