package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;

public class UIDecorate extends UIComponentBase
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorate";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorate";
   
   private String forId;
   
   protected static UIComponent getDecoration(String name, UIComponent component)
   {
      UIComponent dec = component.getFacet(name);
      if (dec!=null) return dec;
      if ( component.getParent()==null ) return null;
      return getDecoration( name, component.getParent() );
   }
   
   /**
    * A depth-first search for an EditableValueHolder
    */
   protected static UIComponent getEditableValueHolder(UIComponent component)
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
   
   protected static String getInputId(String forId, UIComponent cmp)
   {
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
   
   protected static String getInputClientId(String forId, UIComponent cmp, FacesContext facesContext)
   {
      UIComponent input = getInput(forId, cmp, facesContext);
      return input == null ? null : input.getClientId(facesContext);
   }
   
   protected static UIComponent getInput(String forId, UIComponent cmp, FacesContext facesContext)
   {
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
   
   protected static boolean hasMessage(String forId, UIComponent cmp, FacesContext facesContext)
   {
      String clientId = getInputClientId(forId, cmp, facesContext);
      if (clientId==null)
      {
         return false;
      }
      else
      {
         return facesContext.getMessages(clientId).hasNext();
      }
   }
   
   protected static boolean hasRequired(String forId, UIComponent cmp, FacesContext facesContext)
   {
      EditableValueHolder evh = (EditableValueHolder) getInput(forId, cmp, facesContext);
      if (evh == null)
      {
         return false;
      }
      else
      {
         return evh.isRequired();
      }
   }

   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   protected boolean hasMessage()
   {
      return hasMessage(getFor(), this, getFacesContext());
   }
   
   protected boolean hasRequired()
   {
      return hasRequired(getFor(), this, getFacesContext());
   }

   public String getInputId()
   {
      return getInputId(getFor(), this);
   }
   
   protected UIComponent getInput()
   {
      return getInput(getFor(), this, getFacesContext());
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

   protected UIComponent getDecoration(String name)
   {
      return getDecoration(name, this);
   }

   @Override
   public void encodeBegin(FacesContext context) throws IOException
   {
      super.encodeBegin(context);
      context.getResponseWriter().startElement("span", this);
      context.getResponseWriter().writeAttribute("id", getClientId(context), "id");
      boolean hasMessage = hasMessage();

      UIComponent aroundDecoration = getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = getDecoration("aroundInvalidField");
      UIComponent aroundRequiredDecoration = getDecoration("aroundRequiredField");
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
      if (aroundRequiredDecoration != null && hasRequired())
      {
         aroundRequiredDecoration.setParent(this);
         aroundRequiredDecoration.encodeBegin(context);
      }
   }
   
   @Override
   public void encodeEnd(FacesContext facesContext) throws IOException
   {
      boolean hasMessage = hasMessage();
      UIComponent aroundDecoration = getDecoration("aroundField");
      UIComponent aroundInvalidDecoration = getDecoration("aroundInvalidField");
      UIComponent aroundRequiredDecoration = getDecoration("aroundRequiredField");
      if (aroundRequiredDecoration != null)
      {
         EditableValueHolder evh = (EditableValueHolder) getEditableValueHolder(this);
         if (evh != null && evh.isRequired())
         {
            aroundRequiredDecoration.setParent(this);
            aroundRequiredDecoration.encodeEnd(facesContext);
         }
      }
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(this);
         aroundDecoration.encodeEnd(facesContext);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(this);
         aroundInvalidDecoration.encodeEnd(facesContext);
      }
      facesContext.getResponseWriter().endElement("span");
      super.encodeEnd(facesContext);
   }

   @Override
   public void encodeChildren(FacesContext facesContext) throws IOException
   {
      boolean hasMessage = hasMessage();

      UIComponent beforeDecoration = getDecoration("beforeField");
      UIComponent beforeInvalidDecoration = getDecoration("beforeInvalidField");
      UIComponent beforeRequiredDecoration = getDecoration("beforeRequiredField");
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
      if ( beforeRequiredDecoration != null)
      {
         EditableValueHolder evh = (EditableValueHolder) getEditableValueHolder(this);
         if (evh != null && evh.isRequired())
         {
            beforeRequiredDecoration.setParent(this);
            JSF.renderChild(facesContext, beforeRequiredDecoration);
         }
      }
      
      JSF.renderChildren(facesContext, this);
      
      UIComponent afterDecoration = getDecoration("afterField");
      UIComponent afterInvalidDecoration = getDecoration("afterInvalidField");
      UIComponent afterRequiredDecoration = getDecoration("afterRequiredDecoration");
      if ( afterRequiredDecoration != null)
      {
         EditableValueHolder evh = (EditableValueHolder) getEditableValueHolder(this);
         if (evh != null && evh.isRequired())
         {
            afterRequiredDecoration.setParent(this);
            JSF.renderChild(facesContext, afterRequiredDecoration);
         }
      }
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
