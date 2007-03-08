package org.jboss.seam.ui;
import javax.faces.component.UIParameter;
import javax.faces.context.FacesContext;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Pages;
import org.jboss.seam.core.Manager;
import org.jboss.seam.pages.Page;
public class UIConversationId extends UIParameter
{
   private String viewId;
   public static final String COMPONENT_TYPE = "org.jboss.seam.ui.UIConversationId";
   
   public UIConversationId()
   {
      
   }
   
   public UIConversationId(String viewId)
   {
      this.viewId = viewId;
   }
   
   @Override
   public String getName()
   {        
      if (viewId != null && !Manager.instance().isLongRunningConversation())
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
      if (viewId != null && !Manager.instance().isLongRunningConversation())
      {
         Page page = Pages.instance().getPage(viewId);
         return page.getConversationIdParameter().getParameterValue();
      }
      else
      {      
         Conversation conversation = Conversation.instance();
         if ( !conversation.isNested() || conversation.isLongRunning() )
         {
            return conversation.getId();
         }
         else
         {
            return conversation.getParentId();
         }
      }
   }
}
