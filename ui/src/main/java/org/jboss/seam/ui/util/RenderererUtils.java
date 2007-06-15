package org.jboss.seam.ui.util;

import java.io.IOException;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class RenderererUtils
{
   
   public static void renderChildren(FacesContext facesContext,
            UIComponent component) throws IOException
      {
         List children = component.getChildren();
         for (int j=0, size = component.getChildCount(); j<size; j++)
         {
            UIComponent child = (UIComponent) children.get(j);
            renderChild(facesContext, child);
         }
      }

      public static void renderChild(FacesContext facesContext, UIComponent child)
            throws IOException
      {
         if ( child.isRendered() )
         {
            child.encodeBegin(facesContext);
            if ( child.getRendersChildren() )
            {
               child.encodeChildren(facesContext);
            } 
            else
            {
               renderChildren(facesContext, child);
            }
            child.encodeEnd(facesContext);
         }
      }

}
