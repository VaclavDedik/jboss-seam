package org.jboss.seam.debug;

import java.io.PrintWriter;
import java.io.StringWriter;
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
   
   public String[] getBusinessProcess()
   {
      if ( org.jboss.seam.contexts.Contexts.isBusinessProcessContextActive() )
      {
         String[] names = org.jboss.seam.contexts.Contexts.getBusinessProcessContext().getNames();
         Arrays.sort(names);
         return names;
      }
      else
      {
         return null;
      }
   }
   
   public ConversationEntry[] getConversationEntries()
   {
      return Manager.instance().getConversationIdEntryMap().values().toArray( new ConversationEntry[0] );
   }
   
   public String getStackTrace()
   {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      getException().printStackTrace(pw);
      return sw.toString();
   }

   private Exception getException()
   {
      return (Exception) org.jboss.seam.contexts.Contexts.getConversationContext().get("org.jboss.seam.lastException");
   }
   
   public boolean isException()
   {
      return getException()!=null;
   }

}
