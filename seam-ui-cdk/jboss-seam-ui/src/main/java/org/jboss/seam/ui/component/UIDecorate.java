package org.jboss.seam.ui.component;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public abstract class UIDecorate extends UIComponentBase
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorate";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorate";

   public static boolean hasMessage(UIComponent component, FacesContext context)
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

   public static boolean hasRequired(UIComponent component, FacesContext context)
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

   public boolean hasMessage()
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

   public abstract String getFor();

   public abstract void setFor(String forId);

   public UIComponent getDecoration(String name)
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
}