//$Id$
package org.jboss.seam.components;

import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * A Seam component that holds Seam configuration settings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Name("seamSettings")
public class Settings
{
   private static final Logger log = Logger.getLogger(Settings.class);
   
   private static final String CONVERSATION_TIMEOUT = "org.jboss.seam.conversationTimeout";
   private static final String PERSISTENCE_UNIT_NAMES = "org.jboss.seam.persistenceUnitNames";
   private static final String COMPONENT_CLASS_NAMES = "org.jboss.seam.componentClassNames";
   
   private int conversationTimeout;
   private String[] persistenceUnitNames;
   private String[] componentClassNames;
   
   public void init(ServletContext context)
   {
      String timeoutString = context.getInitParameter(CONVERSATION_TIMEOUT);
      //default 10 mins
      conversationTimeout = timeoutString==null ? 600000 : Integer.parseInt(timeoutString);
      log.info("conversation timeout: " + conversationTimeout);

      String ccNamesString = context.getInitParameter(COMPONENT_CLASS_NAMES);
      log.info("component class names: " + ccNamesString);
      componentClassNames = toArray(ccNamesString);

      String puNamesString = context.getInitParameter(PERSISTENCE_UNIT_NAMES);
      log.info("persistence unit names: " + puNamesString);
      persistenceUnitNames = toArray(puNamesString);
   }

   private String[] toArray(String puNamesString)
   {
      if (puNamesString==null)
      {
         return new String[0];
      }
      else
      {      
         StringTokenizer tokens = new StringTokenizer(puNamesString, " ,");
         String[] result = new String[ tokens.countTokens() ];
         int i=0;
         while ( tokens.hasMoreTokens() )
         {
            result[i++] = tokens.nextToken();
         }
         return result;
      }
   }
   
   public int getConversationTimeout()
   {
      return conversationTimeout;
   }
   public void setConversationTimeout(int conversationTimeout)
   {
      this.conversationTimeout = conversationTimeout;
   }
   
   public String[] getPersistenceUnitNames()
   {
      return persistenceUnitNames;
   }
   public void setPersistenceUnitNames(String[] persistenceUnitNames)
   {
      this.persistenceUnitNames = persistenceUnitNames;
   }

   public String[] getComponentClassNames()
   {
      return componentClassNames;
   }
   public void setComponentClassNames(String[] componentClassNames)
   {
      this.componentClassNames = componentClassNames;
   }
   
}
