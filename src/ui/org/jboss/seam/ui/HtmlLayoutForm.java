package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class HtmlLayoutForm extends UIStyleDecoration
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLayoutForm";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.LayoutForm";
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }
   
   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.endElement("div");
   }
   
   @Override
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("div", this);
      writer.writeAttribute("style", "display: table;", null);
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
      writer.writeAttribute("style", "display: table-row;", null);
      
      writer.startElement("div", child);
      writer.writeAttribute("style", "display: table-cell; vertical-align: top", null);  
      renderLabel(facesContext, child);
      UIComponent belowLabel = child.getFacet("belowLabel");
      if (belowLabel != null)
      {
         writer.startElement("div", this);
         JSF.renderChild(facesContext, belowLabel);
         writer.endElement("div");
      }
      writer.endElement("div");
      
      writer.startElement("div", this);
      writer.writeAttribute("style", "display: table-cell;", null);
      JSF.renderChild(facesContext, child);
      UIComponent belowField = child.getFacet("belowField");
      if (belowField != null)
      {
         writer.startElement("div", this);
         JSF.renderChild(facesContext, belowField);
         writer.endElement("div");
      }
      writer.endElement("div");
      
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

}
