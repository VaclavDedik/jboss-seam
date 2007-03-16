package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class HtmlLayoutForm extends UIStyleDecoration
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLayoutForm";

   @Override
   public String getElement()
   {
      return HTML.TABLE_ELEM;
   }
   
   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
     
      for (Object child : this.getChildren())
      {
         if (child instanceof UIComponent)
         {
            writeRow(context, (UIComponent) child);
         }
            
      }
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   private void writeRow(FacesContext facesContext, UIComponent child) throws IOException
   {
      UIComponent belowField = child.getFacet("belowField");
      UIComponent belowLabel = child.getFacet("belowLabel");
      
      ResponseWriter writer = facesContext.getResponseWriter();
      
      

      if (child instanceof UIDecorate)
      {     
         writer.startElement(HTML.TR_ELEM, child);
         writer.startElement(HTML.TD_ELEM, child);
         
         writeLabel(facesContext, (UIDecorate) child);
         
         writer.endElement(HTML.TD_ELEM);
         writer.startElement(HTML.TD_ELEM, child);
         JSF.renderChild(facesContext, child);
         writer.endElement(HTML.TD_ELEM);
         writer.endElement(HTML.TR_ELEM);
         if (belowLabel != null || belowField != null)
         {
            writer.startElement(HTML.TR_ELEM, this);
            if (belowLabel != null)
            {
               writer.startElement(HTML.TD_ELEM, belowLabel);
               JSF.renderChild(facesContext, belowLabel);
               writer.endElement(HTML.TD_ELEM);
            }
            else
            {
               writer.startElement(HTML.TD_ELEM, this);
               writer.endElement(HTML.TD_ELEM);
            }
            if (belowField != null)
            {
               writer.startElement(HTML.TD_ELEM, belowField);
               JSF.renderChild(facesContext, belowField);
               writer.endElement(HTML.TD_ELEM);
            }
            else
            {
               writer.startElement(HTML.TD_ELEM, this);
               writer.endElement(HTML.TD_ELEM);
            }
            writer.endElement(HTML.TR_ELEM);
         }
      }
      else
      {
         writer.startElement(HTML.TR_ELEM, this);
         writer.startElement(HTML.TD_ELEM, child);
         writer.writeAttribute(HTML.COLSPAN_ATTR, "2", HTML.COLSPAN_ATTR);
         JSF.renderChild(facesContext, child);
         writer.endElement(HTML.TD_ELEM);
         writer.endElement(HTML.TR_ELEM);
         if (belowField != null)
         {
            writer.startElement(HTML.TD_ELEM, belowField);
            writer.writeAttribute(HTML.COLSPAN_ATTR, "2", HTML.COLSPAN_ATTR);
            JSF.renderChild(facesContext, belowField);
            writer.endElement(HTML.TD_ELEM);
         }
      }
   }
   
   private void writeLabel(FacesContext facesContext, UIDecorate child) throws IOException
   {
      ResponseWriter writer = facesContext.getResponseWriter();
      
      // Write out a label element
      UIComponent label = child.getFacet("label");
      if (label != null)
      {
         writer.startElement(HTML.LABEL_ELEM, label);
         writer.writeAttribute(HTML.FOR_ATTR, child.getInput().getClientId(facesContext), HTML.FOR_ATTR);
         
         boolean hasMessage = child.hasMessage();
         boolean hasRequired = child.hasRequired();
         
         UIComponent aroundLabelDecoration = child.getDecoration("aroundLabel");
         UIComponent aroundInvalidLabelDecoration = child.getDecoration("aroundInvalidLabel");
         UIComponent aroundRequiredLabelDecoration = child.getDecoration("aroundRequiredLabel");
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
         
         UIComponent beforeLabelDecoration =  child.getDecoration("beforeLabel");
         UIComponent beforeInvalidLabelDecoration =  child.getDecoration("beforeInvalidLabel");
         UIComponent beforeRequiredLabelDecoration =  child.getDecoration("beforeRequiredLabel");
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
         
         JSF.renderChild(facesContext, label);
         
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
