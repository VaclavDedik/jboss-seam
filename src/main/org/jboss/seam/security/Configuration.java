package org.jboss.seam.security;

import java.util.HashMap;

import javax.security.auth.login.AppConfigurationEntry;
import javax.security.auth.login.AppConfigurationEntry.LoginModuleControlFlag;

import org.jboss.seam.Component;
import org.jboss.seam.InterceptionType;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Unwrap;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.security.jaas.SeamLoginModule;

@Name("org.jboss.seam.security.configuration")
@Intercept(InterceptionType.NEVER)
@Scope(ScopeType.APPLICATION)
public class Configuration
{
   static final String DEFAULT_JAAS_CONFIG_NAME = "default";   

   private javax.security.auth.login.Configuration configuration;

   @Create
   public void init()
   {
      configuration = new javax.security.auth.login.Configuration()
      {
         private AppConfigurationEntry[] aces = { new AppConfigurationEntry( 
                  SeamLoginModule.class.getName(), 
                  LoginModuleControlFlag.REQUIRED, 
                  new HashMap<String,String>() 
               ) };
         @Override
         public AppConfigurationEntry[] getAppConfigurationEntry(String name)
         {
            return DEFAULT_JAAS_CONFIG_NAME.equals(name) ? aces : null;
         }
         @Override
         public void refresh() {}
      };
   }
   
   @Unwrap
   public javax.security.auth.login.Configuration getConfiguration()
   {
      return configuration;
   }

   public static javax.security.auth.login.Configuration instance()
   {
      if ( !Contexts.isApplicationContextActive() )
      {
         throw new IllegalStateException("No active application scope");
      }
      return (javax.security.auth.login.Configuration) Component.getInstance(Configuration.class);
   }
}
