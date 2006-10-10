package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.PAGE)
@Name("conversationList")
@Intercept(NEVER)
public class ConversationList implements Serializable {
   
   private List<ConversationEntry> conversationEntryList;
   
   @Create
   public void createConversationEntryList()
   {
      ConversationEntries conversationEntries = ConversationEntries.instance();
      Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
      orderedEntries.addAll( conversationEntries.getConversationEntries() );
      conversationEntryList = new ArrayList<ConversationEntry>( conversationEntries.size() );
      for ( ConversationEntry entry: orderedEntries )
      {
         if ( entry.isDisplayable() && !Seam.isSessionInvalid() )
         {
            conversationEntryList.add(entry);
         }
      }
   }
   
   @Unwrap
   public List<ConversationEntry> getConversationEntryList()
   {
      return conversationEntryList;
   }
}
