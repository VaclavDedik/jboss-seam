//$Id$
package org.jboss.seam;

import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;

import org.jboss.logging.Logger;

public final class Environment
{
   private static final Logger log = Logger.getLogger(Environment.class);
   
   private static final String CONVERSATION_TIMEOUT = "org.jboss.seam.conversationTimeout";
   private static final String PERSISTENCE_UNIT_NAMES = "org.jboss.seam.persistenceUnitNames";
   
   private static final int conversationTimeout;
   private static final String[] persistenceUnitNames;
   
   public static int getConversationTimeout()
   {
      return conversationTimeout;
   }
   
   public static String[] getPersistenceUnitNames()
   {
      return persistenceUnitNames;
   }
   
   static 
   {
      Properties props = new Properties();
      props.putAll( System.getProperties() );
      InputStream stream = Seam.class.getResourceAsStream("/seam.properties");
      if (stream!=null)
      {
         try
         {
            props.load( stream );
         }
         catch (Exception e)
         {
            log.error("Could not read seam.propeties", e);
         }
      }
      
      String timeoutString = props.getProperty(CONVERSATION_TIMEOUT, "600000"); //default 10 mins
      conversationTimeout = Integer.parseInt(timeoutString);
      log.info("conversation timeout: " + conversationTimeout);
      
      String puNamesString = props.getProperty(PERSISTENCE_UNIT_NAMES);
      log.info("persistence unit names: " + puNamesString);
      if (puNamesString==null)
      {
         persistenceUnitNames = new String[0];
      }
      else
      {      
         StringTokenizer tokens = new StringTokenizer(puNamesString, " ,");
         persistenceUnitNames = new String[ tokens.countTokens() ];
         int i=0;
         while ( tokens.hasMoreTokens() )
         {
            persistenceUnitNames[i++] = tokens.nextToken();
         }
      }
   }
   
   Environment() {}
   
}
