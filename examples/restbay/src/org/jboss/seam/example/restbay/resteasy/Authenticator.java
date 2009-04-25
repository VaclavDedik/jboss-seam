package org.jboss.seam.example.restbay.resteasy;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;
import org.jboss.seam.security.Identity;

@Name("authenticator")
@Scope(ScopeType.EVENT)
public class Authenticator
{

   @In
   private Identity identity;
   @In
   private Credentials credentials;
   @Logger
   private Log log;

   public boolean authenticate()
   {
      if (credentials.getUsername().equals(credentials.getPassword())) {
         log.info("Authenticated {0}", credentials.getUsername());
         
         if (credentials.getUsername().equals("admin")) {
            identity.addRole("admin");
            log.info("Admin rights granted for {0}", credentials.getUsername());
         }
         return true;
      } else {
         return false;
      }
   }
}