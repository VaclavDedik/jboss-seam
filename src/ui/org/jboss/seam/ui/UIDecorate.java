package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;

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

   private boolean hasMessage()
   {
      String id = getInputId();
      
      if (id==null) 
      {
         return false;
      }
      else
      {
         UIComponent component = findComponent(id);
         return component==null ? null : 
            getFacesContext().getMessages( component.getClientId( getFacesContext() ) ).hasNext();
      }
   }

   public String getInputId()
   {
      String id = getFor();
      if (id==null)
      {
         for (Object child: getChildren())
         {
            if (child instanceof UIInput)
            {
               UIInput input = (UIInput) child;
               if ( input.isRendered() )
               {
                  return input.getId();
               }
            }
         }
         return null;
      }
      else
      {
         return id;
      }
   }

   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   private UIDecorations getParentDecorations(UIComponent component)
   {
      if (component instanceof UIDecorations) 
      {
         return (UIDecorations) component;
      }
      else if ( component.getParent()==null )
      {
         return null;
      }
      else
      {
         return getParentDecorations( component.getParent() );
      }
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
      UIComponent dec = getFacet(name);
      if (dec==null)
      {
         dec = getParentDecorations(this).getFacet(name);
      }
      return dec;
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      super.encodeBegin(context);
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
