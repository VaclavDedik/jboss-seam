package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * Support for "breadcrumbs".
 * 
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.PAGE)
@Name("conversationStack")
@Intercept(NEVER)
public class ConversationStack implements Serializable {
   
   private List<ConversationEntry> conversationEntryStack;
   
   @Create
   public void createConversationEntryStack()
   {
      ConversationEntries conversationEntries = ConversationEntries.instance();
      ConversationEntry currentConversationEntry = Manager.instance().getCurrentConversationEntry();
      if (currentConversationEntry!=null)
      {
         List<String> idStack = currentConversationEntry.getConversationIdStack();
         conversationEntryStack = new ArrayList<ConversationEntry>( conversationEntries.size() );
         ListIterator<String> ids = idStack.listIterator( idStack.size() );
         while ( ids.hasPrevious() )
         {
            ConversationEntry entry = conversationEntries.getConversationEntry( ids.previous() );
            if ( entry.isDisplayable() && !Seam.isSessionInvalid() ) 
            {
               conversationEntryStack.add(entry);
            }
         }
      }
   }
   
   @Unwrap
   public List<ConversationEntry> getConversationEntryStack()
   {
      return conversationEntryStack;
   }
   
}
