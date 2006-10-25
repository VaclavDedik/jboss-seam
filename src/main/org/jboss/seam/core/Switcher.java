package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Lifecycle;

/**
 * Support for the conversation switcher drop-down menu.
 * 
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.PAGE)
@Name("switcher")
@Intercept(NEVER)
public class Switcher implements Serializable {
   
   private List<SelectItem> selectItems;
   
   @Create
   public void createSelectItems()
   {
      ConversationEntries conversationEntries = ConversationEntries.instance();
      Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
      orderedEntries.addAll( conversationEntries.getConversationEntries() );
      selectItems = new ArrayList<SelectItem>( conversationEntries.size() );
      for ( ConversationEntry entry: orderedEntries )
      {
         if ( entry.isDisplayable() && !Seam.isSessionInvalid() )
         {
            selectItems.add( new SelectItem( entry.getId(), entry.getDescription() ) );
         }
      }
   }
   
   public List<SelectItem> getSelectItems()
   {
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
         boolean success = manager.swapConversation(conversationIdOrOutcome);
         if (success)
         {
            resultingConversationIdOrOutcome = manager.getCurrentConversationId();
            Manager.instance().redirect( manager.getCurrentConversationViewId() );
            actualOutcome = "org.jboss.seam.switch";
         }
         else
         {
            actualOutcome = null;
         }
      }
      Lifecycle.resumeConversation( FacesContext.getCurrentInstance().getExternalContext() ); //TODO: remove, unnecessary
      return actualOutcome;
   }
  
}
