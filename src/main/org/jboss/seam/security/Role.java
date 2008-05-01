package org.jboss.seam.security;

/**
 * Represents a user role.  A dynamic role is a special type of role that is assigned to a user
 * based on the contextual state of a permission check.
 *  
 * @author Shane Bryzak
 */
public class Role extends SimplePrincipal
{   
   private boolean dynamic;
   
   public Role(String name)
   {
      super(name);
   }   
   
   public Role(String name, boolean dynamic)
   {
      this(name);
      this.dynamic = true;
   }
   
   public boolean isDynamic()
   {
      return dynamic;
   }
}
