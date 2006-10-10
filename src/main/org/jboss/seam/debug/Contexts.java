package org.jboss.seam.debug;

import java.util.Arrays;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

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

   public Exception getException()
   {
      return (Exception) org.jboss.seam.contexts.Contexts.getConversationContext().get("org.jboss.seam.debug.lastException");
   }
   
   public boolean isExceptionExists()
   {
      return getException()!=null;
   }

}
