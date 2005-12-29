package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;

/**
 * @author Gavin King
 * @version $Revision$
 */
@Scope(ScopeType.APPLICATION)
@Name("conversationList")
@Intercept(NEVER)
public class ConversationList {
   @Unwrap
   public List<ConversationEntry> getConversationList()
   {
      Map<String, ConversationEntry> map = Manager.instance().getConversationIdEntryMap();
      Set<ConversationEntry> orderedEntries = new TreeSet<ConversationEntry>();
      orderedEntries.addAll( map.values() );
      String currentId = Manager.instance().getCurrentConversationId();
      List<ConversationEntry> list = new ArrayList<ConversationEntry>( map.size() );
      boolean isLongRunning = Manager.instance().isLongRunningConversation();
      for ( ConversationEntry entry: orderedEntries )
      {
         if ( isLongRunning || !entry.getId().equals(currentId) )
         {
            list.add(entry);
         }
      }
      return list;
   }
}
