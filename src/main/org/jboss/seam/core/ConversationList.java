package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.seam.ScopeType;
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
      Map<String, ConversationEntry> map = Manager.instance().getConversationIdEntryMap();
      Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
      orderedEntries.addAll( map.values() );
      conversationEntryList = new ArrayList<ConversationEntry>( map.size() );
      for ( ConversationEntry entry: orderedEntries )
      {
         if ( entry.isDisplayable() )
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
