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
import org.jboss.seam.util.Id;

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
      List<SelectItem> selectItems = new ArrayList<SelectItem>();
      String currentId = Manager.instance().getCurrentConversationId();
      boolean isLongRunning = Manager.instance().isLongRunningConversation();
      for ( ConversationEntry entry: map.values() )
      {
         if ( isLongRunning || !entry.getId().equals(currentId) )
         {
            selectItems.add( new SelectItem( entry.getId(), entry.getDescription() ) );
         }
      }
      return selectItems;
   }
   
   private String conversationIdOrOutcome = "new";

   public String getConversationIdOrOutcome() {
      return conversationIdOrOutcome;
   }

   public void setConversationIdOrOutcome(String selectedId) {
      this.conversationIdOrOutcome = selectedId;
   }
   
   public String select()
   {
      boolean isOutcome = conversationIdOrOutcome==null || !Character.isDigit( conversationIdOrOutcome.charAt(0) );
      String id;
      if (isOutcome)
      {
         id = Id.nextId();
         Manager.instance().setLongRunningConversation(false);
      }
      else
      {
         id = conversationIdOrOutcome;
         Manager.instance().setLongRunningConversation(true);
      }
      Manager.instance().setCurrentConversationId(id);
      Lifecycle.resumeConversation( FacesContext.getCurrentInstance().getExternalContext(), id );
      return isOutcome ? conversationIdOrOutcome : Manager.instance().getCurrentConversationOutcome();
   }
  
}
