package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public abstract class UIAbstractDecorate extends UIStyleDecoration
{

   private String labelColumnClass;
   private String fieldColumnClass;
   private String descriptionColumnClass;
   private String messageColumnClass;

   protected static UIComponent getDecoration(String name, UIComponent component)
   {
      UIComponent dec = component.getFacet(name);
      if (dec!=null) return dec;
      if ( component.getParent()==null ) return null;
      return getDecoration( name, component.getParent() );
   }
   
   protected static UIDecorateAll getDecorateAll(UIComponent component)
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

   /**
    * A depth-first search for an EditableValueHolder
    */
   protected static UIComponent getEditableValueHolder(UIComponent component)
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

   protected static String getInputClientId(UIComponent cmp, FacesContext facesContext)
   {
      UIComponent input = getInput(cmp, facesContext);
      return input == null ? null : input.getClientId(facesContext);
   }

   protected static String getInputId(UIComponent cmp)
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

   protected static UIComponent getInput(UIComponent cmp, FacesContext facesContext)
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

   protected static boolean hasMessage(UIComponent cmp, FacesContext facesContext)
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

   protected static boolean hasRequired(UIComponent cmp, FacesContext facesContext)
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
   }

   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   protected abstract void renderContent(FacesContext context, UIComponent child) throws IOException;
   protected abstract void renderChild(FacesContext context, UIComponent child) throws IOException;

   protected void renderChildAndDecorations(FacesContext context, UIComponent child) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      boolean hasMessage = hasMessage(child, context);
      
      UIDecorateAll parent = getDecorateAll(this);
      if (parent!=null)
      {
         if (labelColumnClass==null) labelColumnClass = parent.getLabelColumnClass();
         if (fieldColumnClass==null) fieldColumnClass = parent.getFieldColumnClass();
         if (messageColumnClass==null) messageColumnClass = parent.getMessageColumnClass();
         if (descriptionColumnClass==null) descriptionColumnClass = parent.getDescriptionColumnClass();
      }
      
      UIComponent label = child.getFacet("label");
      if (label!=null)
      {
         writer.startElement("span", child); 
         writer.writeAttribute("class", labelColumnClass, "labelColumnClass");
         renderLabelAndDecorations(context, label, child);
         writer.endElement("span");
      }
      
      writer.startElement("span", this);
      writer.writeAttribute("class", fieldColumnClass, "fieldColumnClass");
      renderChild(context, child);
      writer.endElement("span");
      
      UIComponent message = getDecoration("message", child);
      if (message!=null && hasMessage)
      {
         message.setParent(child);
         writer.startElement("span", this);
         writer.writeAttribute("class", messageColumnClass, "messageColumnClass");
         JSF.renderChild(context, message);
         writer.endElement("span");
      }
   
      UIComponent description = child.getFacet("description");
      if (description != null)
      {
         writer.startElement("span", this);
         writer.writeAttribute("class", descriptionColumnClass, "descriptionColumnClass");
         JSF.renderChild(context, description);
         writer.endElement("span");
      }
   
   }

   protected void renderFieldAndDecorations(FacesContext context, UIComponent child) throws IOException
   {
      boolean hasMessage = hasMessage(child, context);
      boolean hasRequired = hasRequired(child, context);
   
      UIComponent aroundDecoration = getDecoration("aroundField", child);
      UIComponent aroundInvalidDecoration = getDecoration("aroundInvalidField", child);
      UIComponent aroundRequiredDecoration = getDecoration("aroundRequiredField", child);
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(child);
         aroundDecoration.encodeBegin(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(child);
         aroundInvalidDecoration.encodeBegin(context);
      }
      if (aroundRequiredDecoration!=null && hasRequired)
      {
         aroundRequiredDecoration.setParent(child);
         aroundRequiredDecoration.encodeBegin(context);
      }
      
      UIComponent beforeDecoration = getDecoration("beforeField", child);
      UIComponent beforeInvalidDecoration = getDecoration("beforeInvalidField", child);
      UIComponent beforeRequiredDecoration = getDecoration("beforeRequiredField", child);
      if ( beforeDecoration!=null && !hasMessage )
      {
         beforeDecoration.setParent(child);
         JSF.renderChild(context, beforeDecoration);
      }
      if ( beforeInvalidDecoration!=null && hasMessage )
      {
         beforeInvalidDecoration.setParent(child);
         JSF.renderChild(context, beforeInvalidDecoration);
      }
      if ( beforeRequiredDecoration!=null && hasRequired)
      {
         beforeRequiredDecoration.setParent(child);
         JSF.renderChild(context, beforeRequiredDecoration);
      }
      
      renderContent(context, child);
      
      UIComponent afterDecoration = getDecoration("afterField", child);
      UIComponent afterInvalidDecoration = getDecoration("afterInvalidField", child);
      UIComponent afterRequiredDecoration = getDecoration("afterRequiredField", child);
      if ( afterRequiredDecoration!=null && hasRequired)
      {
         afterRequiredDecoration.setParent(child);
         JSF.renderChild(context, afterRequiredDecoration);
      }
      if ( afterDecoration!=null  && !hasMessage )
      {
         afterDecoration.setParent(child);
         JSF.renderChild(context, afterDecoration);
      }
      if ( afterInvalidDecoration!=null && hasMessage )
      {
         afterInvalidDecoration.setParent(child);
         JSF.renderChild(context, afterInvalidDecoration);
      }
      
      if (aroundRequiredDecoration != null && hasRequired)
      {
         aroundRequiredDecoration.setParent(child);
         aroundRequiredDecoration.encodeEnd(context);
      }
      if (aroundDecoration!=null && !hasMessage)
      {
         aroundDecoration.setParent(child);
         aroundDecoration.encodeEnd(context);
      }
      if (aroundInvalidDecoration!=null && hasMessage)
      {
         aroundInvalidDecoration.setParent(child);
         aroundInvalidDecoration.encodeEnd(context);
      }
   }

   private void renderLabelAndDecorations(FacesContext facesContext, UIComponent label, UIComponent child) throws IOException
   {
      ResponseWriter writer = facesContext.getResponseWriter();
      // Write out a label element
      boolean hasMessage = hasMessage(child, facesContext);
      boolean hasRequired = hasRequired(child, facesContext);
      
      UIComponent aroundLabelDecoration = getDecoration("aroundLabel", child);
      UIComponent aroundInvalidLabelDecoration = getDecoration("aroundInvalidLabel", child);
      UIComponent aroundRequiredLabelDecoration = getDecoration("aroundRequiredLabel", child);
      if (aroundLabelDecoration != null && !hasMessage)
      {  
         aroundLabelDecoration.setParent(child);
         aroundLabelDecoration.encodeBegin(facesContext);
      }
      if (aroundInvalidLabelDecoration != null && hasMessage)
      {  
         aroundInvalidLabelDecoration.setParent(child);
         aroundInvalidLabelDecoration.encodeBegin(facesContext);
      }
      if (aroundRequiredLabelDecoration != null && hasRequired)
      {  
         aroundRequiredLabelDecoration.setParent(child);
         aroundRequiredLabelDecoration.encodeBegin(facesContext);
      }
      
      UIComponent beforeLabelDecoration =  getDecoration("beforeLabel", child);
      UIComponent beforeInvalidLabelDecoration =  getDecoration("beforeInvalidLabel", child);
      UIComponent beforeRequiredLabelDecoration =  getDecoration("beforeRequiredLabel", child);
      if (beforeLabelDecoration != null && !hasMessage)
      {  
         beforeLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, beforeLabelDecoration);
      }
      if (beforeInvalidLabelDecoration != null && hasMessage)
      {  
         beforeInvalidLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, beforeInvalidLabelDecoration);
      }
      if (beforeRequiredLabelDecoration != null && hasRequired)
      {  
         beforeRequiredLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, beforeRequiredLabelDecoration);
      }
      
      String inputClientId = getInputClientId(child, facesContext);
      if (inputClientId!=null)
      {
         writer.startElement("label", label);
         writer.writeAttribute("for", inputClientId, "for");
      }
      JSF.renderChild(facesContext, label);
      if (inputClientId!=null)
      {
         writer.endElement("label");
      }
      
      UIComponent afterLabelDecoration = getDecoration("afterLabel", child);
      UIComponent afterInvalidLabelDecoration = getDecoration("afterInvalidLabel", child);
      UIComponent afterRequiredLabelDecoration = getDecoration("afterRequiredLabel", child);
      if (afterRequiredLabelDecoration != null && hasRequired)
      {  
         afterRequiredLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, afterRequiredLabelDecoration);
      }
      if (afterLabelDecoration != null && !hasMessage)
      {  
         afterLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, afterLabelDecoration);
      }
      if (afterInvalidLabelDecoration != null && hasMessage)
      {  
         afterInvalidLabelDecoration.setParent(child);
         JSF.renderChild(facesContext, afterInvalidLabelDecoration);
      }
      
      if (aroundRequiredLabelDecoration != null && hasRequired)
      {  
         aroundRequiredLabelDecoration.setParent(child);
         aroundRequiredLabelDecoration.encodeEnd(facesContext);
      }
      if (aroundLabelDecoration != null && !hasMessage)
      {  
         aroundLabelDecoration.setParent(child);
         aroundLabelDecoration.encodeEnd(facesContext);
      }
      if (aroundInvalidLabelDecoration != null && hasMessage)
      {  
         aroundInvalidLabelDecoration.setParent(child);
         aroundInvalidLabelDecoration.encodeEnd(facesContext);
      }
      
   }

   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] array = (Object[]) state;
      super.restoreState(context, array[0]);
      labelColumnClass = (String) array[1];
      fieldColumnClass = (String) array[2];
      descriptionColumnClass = (String) array[3];
      messageColumnClass = (String) array[4];
   }

   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[5];
      state[0] = super.saveState(context);
      state[1] = labelColumnClass;
      state[2] = fieldColumnClass;
      state[3] = descriptionColumnClass;
      state[4] = messageColumnClass;
      return state;
   }

   public String getLabelColumnClass()
   {
      return labelColumnClass;
   }

   public void setLabelColumnClass(String leftClass)
   {
      this.labelColumnClass = leftClass;
   }

   public String getFieldColumnClass()
   {
      return fieldColumnClass;
   }

   public void setFieldColumnClass(String rightClass)
   {
      this.fieldColumnClass = rightClass;
   }

   public String getDescriptionColumnClass()
   {
      return descriptionColumnClass;
   }

   public void setDescriptionColumnClass(String descriptionColumnClass)
   {
      this.descriptionColumnClass = descriptionColumnClass;
   }

   public String getMessageColumnClass()
   {
      return messageColumnClass;
   }

   public void setMessageColumnClass(String messageColumnClass)
   {
      this.messageColumnClass = messageColumnClass;
   }


}
