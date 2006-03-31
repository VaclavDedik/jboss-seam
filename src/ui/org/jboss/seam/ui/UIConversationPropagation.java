package org.jboss.seam.ui;

import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;

public class UIConversationPropagation extends UIParameter
{
   
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIConversationPropagation";
   
   private String pageflow;
   private String type = "none";
   
   @Override
   public String getName()
   {
      return "conversationPropagation";
   }

   @Override
   public Object getValue()
   {
      return pageflow==null ? type : type + "." + pageflow;
   }

   public String getPageflow()
   {
      return pageflow;
   }

   public void setPageflow(String pageflow)
   {
      this.pageflow = pageflow;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   @Override
   public void restoreState(FacesContext context, Object state) {
      Object[] values = (Object[]) state;
      super.restoreState(context, values[0]);
      type = (String) values[1];
      pageflow = (String) values[2];
   }

   @Override
   public Object saveState(FacesContext context) {
      Object[] values = new Object[3];
      values[0] = super.saveState(context);
      values[1] = type;
      values[2] = pageflow;
      return values;
   }
}
