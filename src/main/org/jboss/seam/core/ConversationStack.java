package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.jboss.seam.ScopeType;
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
@Scope(ScopeType.APPLICATION)
@Name("conversationStack")
@Intercept(NEVER)
public class ConversationStack {
   @Unwrap
   public List<ConversationEntry> getConversationList()
   {
      Manager manager = Manager.instance();
      Map<String, ConversationEntry> map = manager.getConversationIdEntryMap();
      ConversationEntry currentConversationEntry = manager.getCurrentConversationEntry();
      if (currentConversationEntry==null) return null;
      LinkedList<String> idStack = currentConversationEntry.getConversationIdStack();
      List<ConversationEntry> list = new ArrayList<ConversationEntry>( map.size() );
      ListIterator<String> ids = idStack.listIterator( idStack.size() );
      while ( ids.hasPrevious() )
      {
         ConversationEntry entry = map.get( ids.previous() );
         if ( entry.isDisplayable() ) list.add(entry);
      }
      return list;
   }
   
}
