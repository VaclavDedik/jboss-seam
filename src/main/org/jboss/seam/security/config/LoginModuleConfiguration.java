package org.jboss.seam.security.config;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.Configuration;

/**
 * Implementation of a LoginModule Configuration.
 * 
 * @author Shane Bryzak
 */
public class LoginModuleConfiguration extends Configuration
{
   private Map<String,AppConfigurationEntry[]> entries = new HashMap<String,AppConfigurationEntry[]>();

   public void addEntry(String name, AppConfigurationEntry[] value)
   {
      entries.put(name, value);
   }
   
   @Override
   public AppConfigurationEntry[] getAppConfigurationEntry(String name)
   {
      return entries.get(name);
   }

   @Override
   public void refresh()
   {

   }
}
