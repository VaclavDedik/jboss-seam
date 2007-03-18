package org.jboss.seam.ui;

import java.io.IOException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

public class UIDecorate extends UIAbstractDecorate
{

   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIDecorate";
   public static final String COMPONENT_FAMILY = "org.jboss.seam.ui.Decorate";

   private String forId;
   
   @Override
   public void startElement(ResponseWriter writer) throws IOException
   {
      writer.startElement("span", this);
      writer.writeAttribute("id", getClientId( getFacesContext() ), "id");
   }
   
   @Override
   public void endElement(ResponseWriter writer) throws IOException
   {
      writer.endElement("span");
   }
   
   @Override
   public String getFamily()
   {
      return COMPONENT_FAMILY;
   }

   public String getFor()
   {
      return forId;
   }

   public void setFor(String forId)
   {
      this.forId = forId;
   }

   @Override
   public void encodeChildren(FacesContext context) throws IOException
   {
      renderChildAndDecorations(context, this);
   }
   
   @Override
   protected void renderContent(FacesContext context, UIComponent thiz) throws IOException
   {
      JSF.renderChildren(context, this);
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

   @Override
   protected void renderChild(FacesContext context, UIComponent thiz) throws IOException
   {
      renderFieldAndDecorations(context, this);
   }

}
