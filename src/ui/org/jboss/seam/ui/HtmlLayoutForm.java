package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;


public class HtmlLayoutForm extends UIStyleDecoration
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.HtmlLayoutForm";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.LayoutForm";
   
   private String labelColumnWidth = "20%";
   private String fieldColumnWidth;
   
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
      writer.writeAttribute("style", "clear: both;", null);
      
      writer.startElement("span", child);
      writer.writeAttribute("style", "float: left; text-align: right; width: " + labelColumnWidth + ';', null);  
      renderLabel(facesContext, child);
      UIComponent belowLabel = child.getFacet("belowLabel");
      if (belowLabel != null)
      {
         writer.startElement("div", this);
         JSF.renderChild(facesContext, belowLabel);
         writer.endElement("div");
      }
      writer.endElement("span");
      
      writer.startElement("span", this);
      if (fieldColumnWidth==null)
      {
         if ( !labelColumnWidth.endsWith("%") || labelColumnWidth.length()!=3 )
         {
            throw new IllegalStateException("you must explicitly specify fieldColumnWidth or use a percentage labelColumnWidth");
         }
         fieldColumnWidth = String.valueOf( 100 - Integer.parseInt( labelColumnWidth.substring(0, 2) ) ) + '%';
      }
      writer.writeAttribute("style", "float: right; text-align: left; width: " + fieldColumnWidth + ';', null);
      JSF.renderChild(facesContext, child);
      UIComponent belowField = child.getFacet("belowField");
      if (belowField != null)
      {
         writer.startElement("div", this);
         JSF.renderChild(facesContext, belowField);
         writer.endElement("div");
      }
      writer.endElement("span");
      
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

   public String getLabelColumnWidth()
   {
      return labelColumnWidth;
   }

   public void setLabelColumnWidth(String labelColumnWidth)
   {
      this.labelColumnWidth = labelColumnWidth;
   }
   
   @Override
   public void restoreState(FacesContext context, Object state)
   {
      Object[] array = (Object[]) state;
      super.restoreState(context, array[0]);
      labelColumnWidth = (String) array[1];
      fieldColumnWidth = (String) array[2];
   }
   
   @Override
   public Object saveState(FacesContext context)
   {
      Object[] state = new Object[3];
      state[0] = super.saveState(context);
      state[1] = labelColumnWidth;
      state[2] = fieldColumnWidth;
      return state;
   }

   public String getFieldColumnWidth()
   {
      return fieldColumnWidth;
   }

   public void setFieldColumnWidth(String fieldColumnWidth)
   {
      this.fieldColumnWidth = fieldColumnWidth;
   }

}
