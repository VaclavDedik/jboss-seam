package org.jboss.seam.ui;
import javax.faces.component.UIParameter;

import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;
import org.jboss.seam.pages.Page;
public class UIConversationId extends UIParameter
{
   private String viewId;
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIConversationId";
   
   @Override
   public String getName()
   {
      Conversation conversation = Conversation.instance();
      if (viewId!=null && ( !conversation.isNested() || conversation.isLongRunning() ) )
      {
         Page page = Pages.instance().getPage(viewId);
         return page.getConversationIdParameter().getParameterName();
      }
      else
      {
         return Manager.instance().getConversationIdParameter();
      }
   }
   
   @Override
   public Object getValue()
   {
      Conversation conversation = Conversation.instance();
      if ( !conversation.isNested() || conversation.isLongRunning() )
      {
         if (viewId!=null)
         {
            Page page = Pages.instance().getPage(viewId);
            return page.getConversationIdParameter().getParameterValue();
         }
         else
         {
            return conversation.getId();
         }
      }
      else
      {
         return conversation.getParentId();
      }
   }

   public String getViewId()
   {
      return viewId;
   }

   public void setViewId(String viewId)
   {
      this.viewId = viewId;
   }
}
