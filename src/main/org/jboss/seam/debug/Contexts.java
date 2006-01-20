package org.jboss.seam.debug;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ConversationEntry;
import org.jboss.seam.core.Manager;

@Name("org.jboss.seam.debug.contexts")
@Scope(ScopeType.APPLICATION)
public class Contexts 
{
   public String[] getApplication()
   {
      String[] names = org.jboss.seam.contexts.Contexts.getApplicationContext().getNames();
      Arrays.sort(names);
      return names;
   }

   public String[] getSession()
   {
      String[] names = org.jboss.seam.contexts.Contexts.getSessionContext().getNames();
      Arrays.sort(names);
      return names;
   }

   public String[] getConversation()
   {
      String[] names = org.jboss.seam.contexts.Contexts.getConversationContext().getNames();
      Arrays.sort(names);
      return names;
   }
   
   public ConversationEntry[] getConversationEntries()
   {
      return Manager.instance().getConversationIdEntryMap().values().toArray( new ConversationEntry[0] );
   }

}
