package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;

/**
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.EVENT)
@Name("switcher")
@Intercept(NEVER)
public class Switcher {
   
   public List<SelectItem> getSelectItems()
   {
      Map<String, ConversationEntry> map = Manager.instance().getConversationIdEntryMap();
      List<SelectItem> selectItems = new ArrayList<SelectItem>( map.size() );
      String currentId = Manager.instance().getCurrentConversationId();
      for ( ConversationEntry entry: map.values() )
      {
         if ( entry.getId().equals(currentId) )
         {
            if ( Manager.instance().isLongRunningConversation() )
            {
               Conversation conv = Conversation.instance();
               if ( conv.getDescription()!=null )
               {
                  selectItems.add( new SelectItem( conv.getId(), conv.getDescription() ) );
               }
            }
         }
         else
         {
            if ( entry.getDescription()!=null )
            {
               selectItems.add( new SelectItem( entry.getId(), entry.getDescription() ) );
            }
         }
      }
      return selectItems;
   }
      
   private String conversationIdOrOutcome;
   private String resultingConversationIdOrOutcome;
   
   private String getLongRunningConversationId()
   {
      if ( Manager.instance().isLongRunningConversation() )
      {
         return Manager.instance().getCurrentConversationId();
      }
      else
      {
         List<String> stack = Manager.instance().getCurrentConversationIdStack();
         return stack.size()==1 ? null : stack.get(1); //TODO: is there any way to set it to the current outcome, instead of null?
      }
   }

   public String getConversationIdOrOutcome() {
      return resultingConversationIdOrOutcome==null ? 
            getLongRunningConversationId() :
            resultingConversationIdOrOutcome;
   }

   public void setConversationIdOrOutcome(String selectedId) {
      this.conversationIdOrOutcome = selectedId;
   }
   
   public String select()
   {
      Manager manager = Manager.instance();
      boolean isOutcome = conversationIdOrOutcome==null || !Character.isDigit( conversationIdOrOutcome.charAt(0) );
      String actualOutcome;
      if (isOutcome)
      {
         manager.initializeTemporaryConversation();
         resultingConversationIdOrOutcome = conversationIdOrOutcome;
         actualOutcome = conversationIdOrOutcome;
      }
      else
      {
         manager.swapConversation(conversationIdOrOutcome);
         resultingConversationIdOrOutcome = manager.getCurrentConversationId();
         actualOutcome = manager.getCurrentConversationOutcome();
      }
      Lifecycle.resumeConversation( FacesContext.getCurrentInstance().getExternalContext() );
      return actualOutcome;
   }
  
}
