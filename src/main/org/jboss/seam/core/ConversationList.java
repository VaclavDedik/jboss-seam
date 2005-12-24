package org.jboss.seam.core;

import static org.jboss.seam.InterceptionType.NEVER;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
      Map temp;
      if ( Manager.instance().isLongRunningConversation() )
      {
         temp = map;
      }
      else
      {
         temp = new HashMap<String, ConversationEntry>(map);
         temp.remove( Manager.instance().getCurrentConversationId() );         
      }
      return new ArrayList<ConversationEntry>( temp.values() );
   }
}
