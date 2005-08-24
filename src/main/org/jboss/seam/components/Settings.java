//$Id$
package org.jboss.seam.components;


import javax.servlet.ServletContext;

import org.jboss.logging.Logger;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Strings;

/**
 * A Seam component that holds Seam configuration settings
 * 
 * @author Gavin King
 */
@Scope(ScopeType.APPLICATION)
@Name("org.jboss.seam.components.settings")
public class Settings
{
   private static final Logger log = Logger.getLogger(Settings.class);
   
   public static final String CONVERSATION_TIMEOUT = "org.jboss.seam.conversationTimeout";
   public static final String PERSISTENCE_UNIT_NAMES = "org.jboss.seam.persistenceUnitNames";
   public static final String SESSION_FACTORY_NAMES = "org.jboss.seam.sessionFactoryNames";
   public static final String COMPONENT_CLASS_NAMES = "org.jboss.seam.componentClassNames";
   
   private int conversationTimeout;
   private String[] persistenceUnitNames;
   private String[] sessionFactoryNames;
   private String[] componentClassNames;
   
   public void init(ServletContext context)
   {
      String timeoutString = context.getInitParameter(CONVERSATION_TIMEOUT);
      //default 10 mins
      conversationTimeout = timeoutString==null ? 600000 : Integer.parseInt(timeoutString);
      log.info("conversation timeout: " + conversationTimeout);

      String ccNamesString = context.getInitParameter(COMPONENT_CLASS_NAMES);
      log.info("component class names: " + ccNamesString);
      componentClassNames = Strings.split(ccNamesString, ", ");

      String puNamesString = context.getInitParameter(PERSISTENCE_UNIT_NAMES);
      log.info("persistence unit names: " + puNamesString);
      persistenceUnitNames = Strings.split(puNamesString, ", ");

      String sfNamesString = context.getInitParameter(SESSION_FACTORY_NAMES);
      log.info("session factory names: " + puNamesString);
      sessionFactoryNames = Strings.split(sfNamesString, ", ");
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

   public String[] getSessionFactoryNames()
   {
      return sessionFactoryNames;
   }
   public void setSessionFactoryNames(String[] sessionFactoryNames)
   {
      this.sessionFactoryNames = sessionFactoryNames;
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
