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
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
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
   
   private String userContextDN = "ou=Person,dc=acme,dc=com";
   
   private String roleContextDN = "ou=Role,dc=acme,dc=com";
   
   private String principalDNPrefix = "uid=";
   
   private String principalDNSuffix = ",ou=Person,dc=acme,dc=com";
   
   private String bindDN = "cn=Manager,dc=acme,dc=com";
   
   private String bindCredentials = "secret";
   
   private String userRoleAttribute = "roles";
   
   private boolean roleAttributeIsDN = true;   
   
   private String roleNameAttribute = "cn";
   
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
   
   public String getUserContextDN()
   {
      return userContextDN;
   }
   
   public void setUserContextDN(String userContextDN)
   {
      this.userContextDN = userContextDN;
   }
   
   public String getRoleContextDN()
   {
      return roleContextDN;
   }
   
   public void setRoleContextDN(String roleContextDN)
   {
      this.roleContextDN = roleContextDN;
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
   
   public String getUserRoleAttribute()
   {
      return userRoleAttribute;
   }
   
   public void setUserRoleAttribute(String userRoleAttribute)
   {
      this.userRoleAttribute = userRoleAttribute;
   }
   
   public boolean getRoleAttributeIsDN()
   {
      return roleAttributeIsDN;
   }
   
   public void setRoleAttributeIsDN(boolean value)
   {
      this.roleAttributeIsDN = value;
   }
   
   public String getRoleNameAttribute()
   {
      return roleNameAttribute;
   }
   
   public void setRoleNameAttribute(String roleNameAttribute)
   {
      this.roleNameAttribute = roleNameAttribute;
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
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();
         
         Attributes roleAttribs = new BasicAttributes();
         
         BasicAttribute roleClass = new BasicAttribute("objectClass");
         roleClass.add("organizationalRole");
         
         BasicAttribute roleName = new BasicAttribute(roleNameAttribute);
         roleName.add(role);
         
         roleAttribs.put(roleClass);
         roleAttribs.put(roleName);
         
         String roleDN = String.format("%s=%s,%s", getRoleNameAttribute(), role, roleContextDN);          
         ctx.createSubcontext(roleDN, roleAttribs);
         
         return true;
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Failed to create role", ex);
      }
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
         
         String[] roleAttr = { getUserRoleAttribute() };
                  
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(searchTimeLimit);
         Object[] filterArgs = {name};
         
         NamingEnumeration answer = ctx.search(userContextDN, roleFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute roles = attrs.get( getUserRoleAttribute() );
            for (int r = 0; r < roles.size(); r++)
            {
               Object value = roles.get(r);
               String roleName = null;
               if (roleAttributeIsDN == true)
               {
                  String roleDN = value.toString();
                  String[] returnAttribute = {getRoleNameAttribute()};
                  try
                  {
                     Attributes result2 = ctx.getAttributes(roleDN, returnAttribute);
                     Attribute roles2 = result2.get(getRoleNameAttribute());
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
         
         String[] roleAttr = { getRoleNameAttribute() };
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(searchTimeLimit);
         
         // TODO make these configurable
         String roleFilter = "(objectClass={0})";
         Object[] filterArgs = {"organizationalRole"};
         
         NamingEnumeration answer = ctx.search(roleContextDN, roleFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute user = attrs.get( getRoleNameAttribute() );
            
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
         
         NamingEnumeration answer = ctx.search(userContextDN, userFilter, filterArgs, controls);
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
      return false;
   }

   public boolean userExists(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

}
