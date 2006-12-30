package org.jboss.seam.security.authenticator;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.AuthenticationException;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.provider.AuthenticationProvider;
import org.jboss.seam.util.Reflections;

/**
 * Performs authentication services against one or more providers.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.authenticator")
@Scope(APPLICATION)
@Install(value = false, precedence = BUILT_IN)
public class ProviderAuthenticator extends Authenticator
{
   /**
    * These constants are semantically equivalent to the JAAS login module flags
    * documented in javax.security.auth.login.Configuration
    */
   public enum Flag
   {
      required, requisite, sufficient, optional
   };
   
   private String flags;
   
   private class ProviderEntry 
   {      
      public Object provider;
      public Flag flag;
      
      public ProviderEntry(Object provider, Flag flag)
      {
         this.provider = provider;
         this.flag = flag;
      }
   }

   private List<ProviderEntry> providers = new ArrayList<ProviderEntry>();

   /**
    * 
    * @param authentication Authentication
    * @return Authentication
    * @throws AuthenticationException
    */
   @Override
   public Identity doAuthentication(Identity authentication)
         throws AuthenticationException
   {
      for (ProviderEntry entry : providers)
      {
         AuthenticationProvider provider = null;

         /** todo implement flag logic */
         
         if (entry.provider instanceof AuthenticationProvider)
            provider = (AuthenticationProvider) entry.provider;
         else if (entry.provider instanceof Component)
            provider = (AuthenticationProvider) ((Component) entry.provider).newInstance();

         Identity result = provider.authenticate(authentication);
         if (result != null)
            return result;
      }

      throw new AuthenticationException("Provider not found");
   }
   
   public void setFlags(String flags)
   {
     this.flags = flags;
     
     if (!providers.isEmpty())
        checkFlags();
   }
   
   private void checkFlags()
   {
      if (flags != null)
      {
         String[] parts = flags.split("[,]");
         
         for (int i = 0; i < Math.min(parts.length, providers.size()); i++)
         {
            providers.get(i).flag = Flag.valueOf(parts[i]);
         }
      }
   }

   public void setProviders(Object values)
   {
      if (values instanceof AuthenticationProvider)
      {
         providers.add(new ProviderEntry(values, Flag.required));
      }
      else
      {
         for (Object provider : (List) values)
         {
            if (provider instanceof Component)
               providers.add(new ProviderEntry(provider, Flag.required));
            else
            {
               try
               {
                  provider = Reflections.classForName(provider.toString())
                        .newInstance();
                  providers.add(new ProviderEntry(provider, Flag.required));
               }
               catch (Exception ex)
               {
                  //        log.error("Error creating provider", ex);
               }
            }
         }
      }
      
      checkFlags();
   }
}
