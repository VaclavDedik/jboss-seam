package org.jboss.seam.security;

import java.security.Principal;

/**
 * Simple implementation of the Principal interface, supporting a named user.
 * 
 * @author Shane Bryzak
 */
public class SimplePrincipal implements Principal
{
   private String name;
   
   public SimplePrincipal(String name)
   {
      this.name = name;
   }
   
   public String getName()
   {
      return name;
   }

}
