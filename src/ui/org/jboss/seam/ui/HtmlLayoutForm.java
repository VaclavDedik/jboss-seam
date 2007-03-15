package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.EditableValueHolder;
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
         if (child instanceof UIDecorate)
         {
            UIDecorate decorate = (UIDecorate) child;
            // Get the label facet
            UIComponent label = decorate.getFacet("label");
            UIComponent belowField = decorate.getFacet("belowField");
            UIComponent belowLabel = decorate.getFacet("belowLabel");
            EditableValueHolder evh = (EditableValueHolder) UIDecorate.getEditableValueHolder(decorate);
            writeRow(context, decorate, label, belowField, belowLabel, evh);
         }
         else if (child instanceof UIComponent)
         {
            writeRow(context, (UIComponent) child, null, null, null, null);
         }
      }
   }
   
   @Override
   public boolean getRendersChildren()
   {
      return true;
   }
   
   private void writeRow(FacesContext context, UIComponent child, UIComponent label, UIComponent belowField, UIComponent belowLabel, EditableValueHolder evh) throws IOException
   {
      ResponseWriter writer = context.getResponseWriter();
      writer.startElement(HTML.TR_ELEM, this);
      if (label == null)
      {
         writer.startElement(HTML.TD_ELEM, child);
         writer.writeAttribute(HTML.COLSPAN_ATTR, "2", HTML.COLSPAN_ATTR);
         JSF.renderChild(context, child);
         writer.endElement(HTML.TD_ELEM);
      }
      else
      {
         writer.startElement(HTML.TD_ELEM, label);
         writer.writeAttribute(HTML.ALIGN_ATTR, "right", HTML.ALIGN_ATTR);
         writer.startElement(HTML.LABEL_ELEM, label);
         if (evh != null)
         {
            writer.writeAttribute(HTML.FOR_ATTR, ((UIComponent) evh).getClientId(context), HTML.FOR_ATTR);
            if (evh.isRequired())
            {
               writer.startElement(HTML.SPAN_ELEM, label);
               writer.writeAttribute(HTML.CLASS_ATTR, "required", HTML.CLASS_ATTR);
               writer.write("&lowast;");
               writer.endElement(HTML.SPAN_ELEM);
            }
         }
         JSF.renderChild(context, label);
         writer.endElement(HTML.LABEL_ELEM);
         writer.endElement(HTML.TD_ELEM);
         writer.startElement(HTML.TD_ELEM, child);
         JSF.renderChild(context, child);
         writer.endElement(HTML.TD_ELEM);
      }
      writer.endElement(HTML.TR_ELEM);
      if (belowLabel != null || belowField != null)
      {
         writer.startElement(HTML.TR_ELEM, this);
         if (belowLabel != null)
         {
            writer.startElement(HTML.TD_ELEM, belowLabel);
            JSF.renderChild(context, belowLabel);
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
            JSF.renderChild(context, belowField);
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

}
