package org.jboss.seam.security;

/**
 * An Identity that supports traditional username and password authentication.
 * 
 * @author Shane Bryzak
 */
public class UsernamePasswordToken extends Identity
{
   private static final long serialVersionUID = -891900764186531420L;

   private Role[] roles;

   private Object credentials;

   private Object principal;

   public UsernamePasswordToken(Object principal, Object credentials)
   {
      this.principal = principal;
      this.credentials = credentials;
      this.authenticated = false;
   }

   public UsernamePasswordToken(Object principal, Object credentials,
         Role[] roles)
   {
      this(principal, credentials);
      this.roles = roles;
      this.authenticated = true;
      this.valid = true;
   }

   public String getName()
   {
      return principal.toString();
   }

   @Override
   public Role[] getRoles()
   {
      return roles;
   }

   @Override
   public Object getCredentials()
   {
      return credentials;
   }

   @Override
   public Object getPrincipal()
   {
      return principal;
   }

   @Override
   public String toString()
   {
      return String.format("UsernamePasswordToken[%s]", principal.toString());
   }
}
