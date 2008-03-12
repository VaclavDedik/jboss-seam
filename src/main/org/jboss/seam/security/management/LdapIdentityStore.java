package org.jboss.seam.security.management;

import static org.jboss.seam.ScopeType.APPLICATION;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;

import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

/**
 * An IdentityStore implementation that integrates with a directory service.
 * 
 * @author Shane Bryzak
 */
@Scope(APPLICATION)
@BypassInterceptors
public class LdapIdentityStore implements IdentityStore
{
   
   private String serverAddress = "localhost";
   
   private int serverPort = 389;
   
   private String userCtxDN = "ou=Person,dc=acme,dc=com";
   
   private String roleCtxDN = "ou=Role,dc=acme,dc=com";
   
   private String principalDNPrefix = "uid=";
   
   private String principalDNSuffix = ",ou=Person,dc=acme,dc=com";
   
   private String bindDN;
   
   private String bindCredentials;
      
   // TODO make configurable
   private boolean roleAttributeIsDN = true;
   
   public String getServerAddress()
   {
      return serverAddress;
   }
   
   public void setServerAddress(String serverAddress)
   {
      this.serverAddress = serverAddress;
   }
   
   public int getServerPort()
   {
      return serverPort;
   }
   
   public void setServerPort(int serverPort)
   {
      this.serverPort = serverPort;
   }
   
   public String getUserCtxDN()
   {
      return userCtxDN;
   }
   
   public void setUserCtxDN(String userCtxDN)
   {
      this.userCtxDN = userCtxDN;
   }
   
   public String getRoleCtxDN()
   {
      return roleCtxDN;
   }
   
   public void setRoleCtxDN(String roleCtxDN)
   {
      this.roleCtxDN = roleCtxDN;
   }
   
   public String getPrincipalDNPrefix()
   {
      return principalDNPrefix;
   }
   
   public void setPrincipalDNPrefix(String value)
   {
      this.principalDNPrefix = value;
   }
   
   public String getPrincipalDNSuffix()
   {
      return principalDNSuffix;
   }
   
   public void setPrincipalDNSuffix(String value)
   {
      this.principalDNSuffix = value;
   }
   
   public String getBindDN()
   {
      return bindDN;
   }
   
   public void setBindDN(String bindDN)
   {
      this.bindDN = bindDN;
   }
   
   public String getBindCredentials()
   {
      return bindCredentials;
   }
   
   public void setBindCredentials(String bindCredentials)
   {
      this.bindCredentials = bindCredentials;
   }
   
   protected final InitialLdapContext initialiseContext()
      throws NamingException
   {
      return initialiseContext(bindDN, bindCredentials);
   }
   
   protected final InitialLdapContext initialiseContext(String principal, String credentials)
      throws NamingException
   {
      Properties env = new Properties();

      env.setProperty(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
      env.setProperty(Context.SECURITY_AUTHENTICATION, "simple");
      
      String providerUrl = String.format("ldap://%s:%d", getServerAddress(), getServerPort());
      env.setProperty(Context.PROVIDER_URL, providerUrl);
      
      env.setProperty(Context.SECURITY_PRINCIPAL, principal);
      env.setProperty(Context.SECURITY_CREDENTIALS, credentials);      
      
      InitialLdapContext ctx = new InitialLdapContext(env, null);
      return ctx;
   }
   
   protected String getUserDN(String username)
   {
      return String.format("%s%s%s", getPrincipalDNPrefix(), username, getPrincipalDNSuffix());
   }
      
   public boolean authenticate(String username, String password) 
   {      
      String securityPrincipal = getUserDN(username);
      
      try
      {
         InitialLdapContext ctx = initialiseContext(securityPrincipal, password);   
         ctx.close();
         return true;         
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Authentication error", ex);
      }
   }

   public boolean changePassword(String name, String password) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean createRole(String role) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean createUser(String username, String password) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean deleteRole(String role) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean deleteUser(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean disableUser(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean enableUser(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public List<String> getGrantedRoles(String name) 
   {
      Set<String> userRoles = new HashSet<String>();
      
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();
                  
         String roleFilter = "(uid={0})";
                  
         // TODO make configurable
         int searchScope = SearchControls.SUBTREE_SCOPE;
         int searchTimeLimit = 10000;
         
         // TODO make configurable
         String roleAttrName = "roles";
         String[] roleAttr = {roleAttrName};
         
         // TODO make configurable
         String roleNameAttribute = "cn";
                  
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(searchTimeLimit);
         Object[] filterArgs = {name};
         
         NamingEnumeration answer = ctx.search(userCtxDN, roleFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute roles = attrs.get(roleAttrName);
            for (int r = 0; r < roles.size(); r++)
            {
               Object value = roles.get(r);
               String roleName = null;
               if (roleAttributeIsDN == true)
               {
                  String roleDN = value.toString();
                  String[] returnAttribute = {roleNameAttribute};
                  try
                  {
                     Attributes result2 = ctx.getAttributes(roleDN, returnAttribute);
                     Attribute roles2 = result2.get(roleNameAttribute);
                     if( roles2 != null )
                     {
                        for(int m = 0; m < roles2.size(); m ++)
                        {
                           roleName = (String) roles2.get(m);
                           userRoles.add(roleName);
                        }
                     }
                  }
                  catch (NamingException ex)
                  {
                     throw new IdentityManagementException("Failed to query roles", ex);
                  }
               }
               else
               {
                  // The role attribute value is the role name
                  roleName = value.toString();
                  userRoles.add(roleName);
               }
            }
         }
         answer.close();                     
         
         return new ArrayList<String>(userRoles);         
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Error getting roles", ex);
      }
      finally
      {
         if (ctx != null) 
         {
            try
            {
               ctx.close();
            }
            catch (NamingException ex) {}
         }
      }
   }

   public List<String> getImpliedRoles(String name) 
   {
      return getGrantedRoles(name);
   }

   public boolean grantRole(String name, String role) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean isUserEnabled(String name) 
   {
      // TODO implement this somehow
      return true;
   }

   public List<String> listRoles() 
   {
      List<String> roles = new ArrayList<String>();
      
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();              
         
         // TODO make configurable
         int searchScope = SearchControls.SUBTREE_SCOPE;
         int searchTimeLimit = 10000;
         
         // TODO make configurable
         String roleAttrName = "cn";
         String[] roleAttr = {roleAttrName};
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(searchTimeLimit);
         
         // TODO make these configurable
         String roleFilter = "(objectClass={0})";
         Object[] filterArgs = {"organizationalRole"};
         
         NamingEnumeration answer = ctx.search(roleCtxDN, roleFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute user = attrs.get(roleAttrName);
            
            for (int i = 0; i < user.size(); i++)
            {
               Object value = user.get(i);
               roles.add(value.toString());
            }            
         }
         answer.close();
         return roles;         
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Error getting roles", ex);
      }
      finally
      {
         if (ctx != null) 
         {
            try
            {
               ctx.close();
            }
            catch (NamingException ex) {}
         }
      }
   }

   public List<String> listUsers() 
   {
      List<String> users = new ArrayList<String>();
      
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();              
         
         // TODO make configurable
         int searchScope = SearchControls.SUBTREE_SCOPE;
         int searchTimeLimit = 10000;
         
         // TODO make configurable
         String userAttrName = "uid";
         String[] userAttr = {userAttrName};
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(userAttr);
         controls.setTimeLimit(searchTimeLimit);
         
         // TODO make these configurable
         String userFilter = "(objectClass={0})";
         Object[] filterArgs = {"person"};
         
         NamingEnumeration answer = ctx.search(userCtxDN, userFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute user = attrs.get(userAttrName);
            
            for (int i = 0; i < user.size(); i++)
            {
               Object value = user.get(i);
               users.add(value.toString());
            }            
         }
         answer.close();
         return users;         
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Error getting users", ex);
      }
      finally
      {
         if (ctx != null) 
         {
            try
            {
               ctx.close();
            }
            catch (NamingException ex) {}
         }
      }
   }

   public List<String> listUsers(String filter) 
   {
      // TODO Auto-generated method stub
      return null;
   }

   public boolean revokeRole(String name, String role) 
   {
      // TODO Auto-generated method stub
      return false;
   }

   public boolean roleExists(String name) 
   {
      return true;
   }

   public boolean userExists(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

}
