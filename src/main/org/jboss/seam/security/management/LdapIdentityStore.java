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
   protected FeatureSet featureSet = new FeatureSet(FeatureSet.FEATURE_ALL);
   
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
   
   private String userNameAttribute = "uid";
   
   private String userPasswordAttribute = "userPassword";
   
   private String firstNameAttribute = null;
   
   private String lastNameAttribute = "sn";
   
   private String fullNameAttribute = "cn";
   
   private String roleNameAttribute = "cn";
   
   private String objectClassAttribute = "objectClass";
   
   private String[] roleObjectClasses = { "organizationalRole" };
   
   private String[] userObjectClasses = { "person", "uidObject" };
   
   /**
    * Time limit for LDAP searches, in milliseconds
    */
   private int searchTimeLimit = 10000;
      
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
   
   public String getUserNameAttribute()
   {
      return userNameAttribute;
   }
   
   public void setUserNameAttribute(String userNameAttribute)
   {
      this.userNameAttribute = userNameAttribute;
   }
   
   public String getUserPasswordAttribute()
   {
      return userPasswordAttribute;
   }
   
   public void setUserPasswordAttribute(String userPasswordAttribute)
   {
      this.userPasswordAttribute = userPasswordAttribute;
   }
   
   public String getFirstNameAttribute()
   {
      return firstNameAttribute;
   }
   
   public void setFirstNameAttribute(String firstNameAttribute)
   {
      this.firstNameAttribute = firstNameAttribute;
   }
   
   public String getLastNameAttribute()
   {
      return lastNameAttribute;
   }
   
   public void setLastNameAttribute(String lastNameAttribute)
   {
      this.lastNameAttribute = lastNameAttribute;
   }
   
   public String getFullNameAttribute()
   {
      return fullNameAttribute;
   }
   
   public void setFullNameAttribute(String fullNameAttribute)
   {
      this.fullNameAttribute = fullNameAttribute;
   }
   
   public String getObjectClassAttribute()
   {
      return objectClassAttribute;
   }
   
   public void setObjectClassAttribute(String objectClassAttribute)
   {
      this.objectClassAttribute = objectClassAttribute;
   }
   
   public String[] getRoleObjectClasses()
   {
      return roleObjectClasses;
   }
   
   public void setRoleObjectClass(String[] roleObjectClasses)
   {
      this.roleObjectClasses = roleObjectClasses;
   }
   
   public String[] getUserObjectClasses()
   {
      return userObjectClasses;
   }
   
   public void setUserObjectClasses(String[] userObjectClasses)
   {
      this.userObjectClasses = userObjectClasses;
   }
   
   public int getSearchTimeLimit()
   {
      return searchTimeLimit;
   }
   
   public void setSearchTimeLimit(int searchTimeLimit)
   {
      this.searchTimeLimit = searchTimeLimit;
   }
   
   public int getFeatures()
   {
      return featureSet.getFeatures();
   }
   
   public void setFeatures(int features)
   {
      featureSet = new FeatureSet(features);
   }
   
   public boolean supportsFeature(int feature)
   {
      return featureSet.supports(feature);
   }
   
   protected final InitialLdapContext initialiseContext()
      throws NamingException
   {
      return initialiseContext(getBindDN(), getBindCredentials());
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
         
         BasicAttribute roleClass = new BasicAttribute(getObjectClassAttribute());
         for (String objectClass : getRoleObjectClasses())
         {
            roleClass.add(objectClass);
         }
         
         BasicAttribute roleName = new BasicAttribute(getRoleNameAttribute());
         roleName.add(role);
         
         roleAttribs.put(roleClass);
         roleAttribs.put(roleName);
         
         String roleDN = String.format("%s=%s,%s", getRoleNameAttribute(), role, getRoleContextDN() );          
         ctx.createSubcontext(roleDN, roleAttribs);
         
         return true;
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Failed to create role", ex);
      }
   }
   
   public boolean createUser(String username, String password, String firstname, String lastname)
   {
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();
         
         Attributes userAttribs = new BasicAttributes();
         
         BasicAttribute userClass = new BasicAttribute(getObjectClassAttribute());
         for (String objectClass : getUserObjectClasses())
         {
            userClass.add(objectClass);
         }
         
         BasicAttribute usernameAttrib = new BasicAttribute(getUserNameAttribute());
         usernameAttrib.add(username);
         
         BasicAttribute passwordAttrib = new BasicAttribute(getUserPasswordAttribute());
         passwordAttrib.add(PasswordHash.generateHash(password));
         
         userAttribs.put(userClass);
         userAttribs.put(usernameAttrib);
         userAttribs.put(passwordAttrib);
         
         if (getFirstNameAttribute() != null && firstname != null)
         {
            BasicAttribute firstNameAttrib = new BasicAttribute(getFirstNameAttribute());
            firstNameAttrib.add(firstname);
            userAttribs.put(firstNameAttrib);
         }
         
         if (getLastNameAttribute() != null && lastname != null)
         {
            BasicAttribute lastNameAttrib = new BasicAttribute(getLastNameAttribute());
            lastNameAttrib.add(lastname);
            userAttribs.put(lastNameAttrib);
         }
         
         if (getFullNameAttribute() != null && firstname != null && lastname != null)
         {
            BasicAttribute fullNameAttrib = new BasicAttribute(getFullNameAttribute());
            fullNameAttrib.add(firstname + " " + lastname);
            userAttribs.put(fullNameAttrib);
         }
         
         String userDN = String.format("%s=%s,%s", getUserNameAttribute(), username, getUserContextDN() );          
         ctx.createSubcontext(userDN, userAttribs);
         
         return true;
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Failed to create user", ex);
      }      
   }

   public boolean createUser(String username, String password) 
   {
      return createUser(username, password, null, null);
   }

   public boolean deleteRole(String role) 
   {
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();
                 
         String roleDN = String.format("%s=%s,%s", getRoleNameAttribute(), role, getRoleContextDN() );          
         ctx.destroySubcontext(roleDN);         
         return true;
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Failed to delete role", ex);
      }
   }
   
   public boolean roleExists(String role) 
   {      
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();              
         
         int searchScope = SearchControls.SUBTREE_SCOPE;
         int searchTimeLimit = 10000;
         
         String[] roleAttr = { getRoleNameAttribute() };
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(searchTimeLimit);
         
         String roleFilter = "(&(" + getObjectClassAttribute() + "={0})(" + getRoleNameAttribute() + "={1}))";
         Object[] filterArgs = { getRoleObjectClasses(), role};
         
         NamingEnumeration answer = ctx.search(getRoleContextDN(), roleFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute user = attrs.get( getRoleNameAttribute() );
            
            for (int i = 0; i < user.size(); i++)
            {
               Object value = user.get(i);
               if (role.equals(value)) return true;
            }            
         }
         answer.close();

         return false;
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

   public boolean deleteUser(String name) 
   {
      InitialLdapContext ctx = null;      
      try
      {
         ctx = initialiseContext();
                 
         String userDN = getUserDN(name);          
         ctx.destroySubcontext(userDN);         
         return true;
      }
      catch (NamingException ex)
      {
         throw new IdentityManagementException("Failed to delete user", ex);
      }
   }
   
   public boolean isUserEnabled(String name) 
   {
      // TODO implement this somehow
      return true;
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
                  
         String userFilter = "(" + getUserNameAttribute() + "={0})";
                  
         // TODO make configurable
         int searchScope = SearchControls.SUBTREE_SCOPE;
         
         String[] roleAttr = { getUserRoleAttribute() };
                  
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(getSearchTimeLimit());
         Object[] filterArgs = {name};
         
         NamingEnumeration answer = ctx.search(getUserContextDN(), userFilter, filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute roles = attrs.get( getUserRoleAttribute() );
            if (roles != null)
            {
               for (int r = 0; r < roles.size(); r++)
               {
                  Object value = roles.get(r);
                  String roleName = null;
                  if (getRoleAttributeIsDN() == true)
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
   
   public boolean revokeRole(String name, String role) 
   {
      // TODO Auto-generated method stub
      return false;
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
         
         String[] roleAttr = { getRoleNameAttribute() };
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(roleAttr);
         controls.setTimeLimit(getSearchTimeLimit());
         
         StringBuilder roleFilter = new StringBuilder();
         
         Object[] filterArgs = new Object[getRoleObjectClasses().length];
         for (int i = 0; i < getRoleObjectClasses().length; i++)
         {
            roleFilter.append("(");
            roleFilter.append(getObjectClassAttribute());
            roleFilter.append("={");
            roleFilter.append(i);
            roleFilter.append("})");
            filterArgs[i] = getRoleObjectClasses()[i];
         }         
         
         NamingEnumeration answer = ctx.search( getRoleContextDN(), roleFilter.toString(), 
               filterArgs, controls);
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
         
         String[] userAttr = {getUserNameAttribute()};
                           
         SearchControls controls = new SearchControls();
         controls.setSearchScope(searchScope);
         controls.setReturningAttributes(userAttr);
         controls.setTimeLimit(getSearchTimeLimit());
                  
         StringBuilder userFilter = new StringBuilder();
         
         Object[] filterArgs = new Object[getUserObjectClasses().length];
         for (int i = 0; i < getUserObjectClasses().length; i++)
         {
            userFilter.append("(");
            userFilter.append(getObjectClassAttribute());
            userFilter.append("={");
            userFilter.append(i);
            userFilter.append("})");
            filterArgs[i] = getUserObjectClasses()[i];
         }            
         
         NamingEnumeration answer = ctx.search(getUserContextDN(), userFilter.toString(), filterArgs, controls);
         while (answer.hasMore())
         {
            SearchResult sr = (SearchResult) answer.next();
            Attributes attrs = sr.getAttributes();
            Attribute user = attrs.get(getUserNameAttribute());
            
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

   public boolean userExists(String name) 
   {
      // TODO Auto-generated method stub
      return false;
   }

}
