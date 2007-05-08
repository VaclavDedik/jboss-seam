package org.jboss.seam.ui.util;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.jboss.seam.ui.UIDecorate;

public class Decoration
{

   /**
    * A depth-first search for a rendered EditableValueHolder
    * @param component UIComponent to start search at
    * @return The found EditableValueHolder, null if none found
    */
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
            UIComponent evh = getEditableValueHolder( (UIComponent) child);
            if (evh!=null) return evh;
         }
      }
      return null;
   }

   /**
    * A depth-first search for any FacesMessages on rendered EditableValueHolders
    * @param component UIComponent to start search at
    * @return true if any FacesMessages were found
    * 
    */
   public static boolean isComponentHasFacesMessages(UIComponent component, FacesContext context)
   {
      if ( !component.isRendered() ) return false;
      
      if ( component instanceof EditableValueHolder )
      {
         if ( ! ( (EditableValueHolder) component ).isValid() ) return true;
      }

      for (Object child: component.getChildren())
      {
         if (child instanceof UIComponent)
         {
            boolean message = isComponentHasFacesMessages( (UIComponent) child, context );
            if (message) return true;
         }
      }
      return false;
   }

   /**
    * A depth-first search for any required, rendered EditableValueHolders
    * @param component UIComponent to start search at
    * @return true if any FacesMessages were found
    * 
    */
   public static boolean isComponentRequired(UIComponent component, FacesContext context)
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
            boolean required = isComponentRequired( (UIComponent) child, context );
            if (required) return true;
         }
      }
      return false;
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
   
}
