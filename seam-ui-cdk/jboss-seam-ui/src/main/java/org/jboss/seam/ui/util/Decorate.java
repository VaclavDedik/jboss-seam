package org.jboss.seam.ui.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class Decorate
{

   /*public static UIComponent getDecoration(String name, UIComponent component)
   {
      UIComponent dec = component.getFacet(name);
      if (dec!=null) return dec;
      if ( component.getParent()==null ) return null;
      return getDecoration( name, component.getParent() );
   }
   
   public static UIDecorateAll getDecorateAll(UIComponent component)
   {
      UIComponent parent = component.getParent();
      if (parent==null)
      {
         return null;
      }
      else if ( parent instanceof UIDecorateAll )
      {
         return (UIDecorateAll) parent;
      }
      else
      {
         return getDecorateAll( parent );
      }
   }

   public static UIComponent getEditableValueHolder(UIComponent component)
   {
      if (component instanceof EditableValueHolder)
      {
         return component.isRendered() ? component : null;
      }
      for (Object child: component.getChildren())
      {
         if (child instanceof UIComponent)
         {
            UIComponent evh = getEditableValueHolder( (UIComponent) child );
            if (evh!=null) return evh;
         }
      }
      return null;
   }

   public static String getInputClientId(UIComponent cmp, FacesContext facesContext)
   {
      UIComponent input = getInput(cmp, facesContext);
      return input == null ? null : input.getClientId(facesContext);
   }

   public static String getInputId(UIComponent cmp)
   {
      String forId = cmp instanceof UIDecorate ?
               ( (UIDecorate) cmp ).getFor() : null;
      if (forId==null)
      {
         UIComponent evh = getEditableValueHolder(cmp);
         return evh==null ? null : evh.getId();
      }
      else
      {
         return forId;
      }
   }

   public static UIComponent getInput(UIComponent cmp, FacesContext facesContext)
   {
      String forId = cmp instanceof UIDecorate ?
         ( (UIDecorate) cmp ).getFor() : null;
      if (forId==null)
      {
         UIComponent evh = getEditableValueHolder(cmp);
         return evh==null ? null : evh;
      }
      else
      {
         UIComponent component = cmp.findComponent(forId);
         return component==null ? null : component;
      }
   }

   public static boolean hasMessage(UIComponent cmp, FacesContext facesContext)
   {
      String clientId = getInputClientId(cmp, facesContext);
      if (clientId==null)
      {
         return false;
      }
      else
      {
         return facesContext.getMessages(clientId).hasNext();
      }
   }

   public static boolean hasRequired(UIComponent cmp, FacesContext facesContext)
   {
      EditableValueHolder evh = (EditableValueHolder) getInput(cmp, facesContext);
      if (evh == null)
      {
         return false;
      }
      else
      {
         return evh.isRequired();
      }
   }*/
}
