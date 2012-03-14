package org.jboss.seam.debug;

import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.ConversationEntry;
import org.jboss.seam.util.Exceptions;

@Name("org.jboss.seam.debug.contexts")
@Scope(ScopeType.APPLICATION)
@Install(debug=true, precedence=BUILT_IN)
public class Contexts 
{
   public List<ConversationEntry> getConversationEntries()
   {
      return new ArrayList<ConversationEntry>( ConversationEntries.instance().getConversationEntries() );
   }
   
   public String[] getApplication()
   {
      String[] names = org.jboss.seam.contexts.Contexts.getApplicationContext().getNames();
      Arrays.sort(names);
      return names;
   }

   public String[] getSession()
   {
      String[] names={};
      if(org.jboss.seam.contexts.Contexts.isSessionContextActive()) {
          names = org.jboss.seam.contexts.Contexts.getSessionContext().getNames();
      }
      Arrays.sort(names);
      return names;
   }

   public String[] getConversation()
   {
      String[] names = {};
      if ( org.jboss.seam.contexts.Contexts.isConversationContextActive()) {
         names = org.jboss.seam.contexts.Contexts.getConversationContext().getNames();
      }
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
      Exception e=null;
      if(org.jboss.seam.contexts.Contexts.isConversationContextActive())
      {
         if(org.jboss.seam.contexts.Contexts.getConversationContext().get("org.jboss.seam.caughtException")==null) {
            return null;
         }
         else {
            e=(Exception)org.jboss.seam.contexts.Contexts.getConversationContext().get("org.jboss.seam.caughtException");
         }
      }
      return e;
   }
   
   public List<Exception> getExceptionCauses()
   {
      List<Exception> causes = new ArrayList<Exception>();
      for (Exception cause=getException(); cause!=null; cause=Exceptions.getCause(cause))
      {
         causes.add(cause);
      }
      return causes;
   }
   
   public boolean isExceptionExists()
   {
      return getException()!=null;
   }

}
