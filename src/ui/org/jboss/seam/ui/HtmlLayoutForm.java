package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class HtmlLayoutForm extends UIStyleDecoration
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLayoutForm";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.LayoutForm";
   
   private String labelColumnClass;
   private String fieldColumnClass;
   private String descriptionColumnClass;
   private String rowClass;
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
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
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("div", this);
   }

   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      for (Object child : this.getChildren())
      {
         if (child instanceof UIComponent)
         {
            renderChild( context, (UIComponent) child );
         }
      }
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   private void renderChild(FacesContext facesContext, UIComponent child) throws IOException
   {
      ResponseWriter writer = facesContext.getResponseWriter();
      
      writer.startElement("div", this);
      writer.writeAttribute("class", rowClass, "rowClass");
      
      writer.startElement("span", child); 
      writer.writeAttribute("class", labelColumnClass, "labelColumnClass");
      renderLabel(facesContext, child);
      writer.endElement("span");
      
      writer.startElement("span", this);
      writer.writeAttribute("class", fieldColumnClass, "fieldColumnClass");
      JSF.renderChild(facesContext, child);
      writer.endElement("span");
      
      UIComponent description = child.getFacet("description");
      if (description != null)
      {
         writer.startElement("span", this);
         writer.writeAttribute("class", descriptionColumnClass, "descriptionColumnClass");
         JSF.renderChild(facesContext, description);
         writer.endElement("span");
      }

      writer.endElement("div");
   }

   private void renderLabel(FacesContext facesContext, UIComponent child) throws IOException
   {
      ResponseWriter writer = facesContext.getResponseWriter();
      // Write out a label element
      UIComponent label = child.getFacet("label");
      if (label != null)
      {
         boolean hasMessage = UIDecorate.hasMessage(child, facesContext);
         boolean hasRequired = UIDecorate.hasRequired(child, facesContext);
         
         UIComponent aroundLabelDecoration = UIDecorate.getDecoration("aroundLabel", child);
         UIComponent aroundInvalidLabelDecoration = UIDecorate.getDecoration("aroundInvalidLabel", child);
         UIComponent aroundRequiredLabelDecoration = UIDecorate.getDecoration("aroundRequiredLabel", child);
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
         
         UIComponent beforeLabelDecoration =  UIDecorate.getDecoration("beforeLabel", child);
         UIComponent beforeInvalidLabelDecoration =  UIDecorate.getDecoration("beforeInvalidLabel", child);
         UIComponent beforeRequiredLabelDecoration =  UIDecorate.getDecoration("beforeRequiredLabel", child);
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
         
         String inputClientId = UIDecorate.getInputClientId(child, facesContext);
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
         
         UIComponent afterLabelDecoration = UIDecorate.getDecoration("afterLabel", child);
         UIComponent afterInvalidLabelDecoration = UIDecorate.getDecoration("afterInvalidLabel", child);
         UIComponent afterRequiredLabelDecoration = UIDecorate.getDecoration("afterRequiredLabel", child);
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
      
   }
   
   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] array = (Object[]) state;
      super.restoreState(context, array[0]);
      labelColumnClass = (String) array[1];
      fieldColumnClass = (String) array[2];
      rowClass = (String) array[3];
   }
   
   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[4];
      state[0] = super.saveState(context);
      state[1] = labelColumnClass;
      state[2] = fieldColumnClass;
      state[3] = rowClass;
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

   public String getRowClass()
   {
      return rowClass;
   }

   public void setRowClass(String rowClass)
   {
      this.rowClass = rowClass;
   }

   public String getDescriptionColumnClass()
   {
      return descriptionColumnClass;
   }

   public void setDescriptionColumnClass(String descriptionColumnClass)
   {
      this.descriptionColumnClass = descriptionColumnClass;
   }


}
