package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodExpression;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.persistence.PersistenceProvider;
import org.jboss.seam.util.Strings;
import org.jboss.seam.web.Session;

/**
 * API for authorization and authentication via
 * Seam security. This base implementation 
 * supports role-based authorization only.
 * Subclasses may add more sophisticated 
 * permissioning mechanisms.
 * 
 * @author Shane Bryzak
 *
 */
@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Install(precedence = BUILT_IN)
@BypassInterceptors
@Startup
public class Identity implements Serializable
{  
   public static final String EVENT_INIT_IDENTITY = "org.jboss.seam.security.initIdentity";
   public static final String EVENT_LOGIN_SUCCESSFUL = "org.jboss.seam.security.loginSuccessful";
   public static final String EVENT_LOGIN_FAILED = "org.jboss.seam.security.loginFailed";
   public static final String EVENT_NOT_LOGGED_IN = "org.jboss.seam.security.notLoggedIn";
   public static final String EVENT_NOT_AUTHORIZED = "org.jboss.seam.security.notAuthorized";
   public static final String EVENT_PRE_AUTHENTICATE = "org.jboss.seam.security.preAuthenticate";
   public static final String EVENT_POST_AUTHENTICATE = "org.jboss.seam.security.postAuthenticate";
   public static final String EVENT_LOGGED_OUT = "org.jboss.seam.security.loggedOut";
   public static final String EVENT_CREDENTIALS_UPDATED = "org.jboss.seam.security.credentialsUpdated";
   public static final String EVENT_REMEMBER_ME = "org.jboss.seam.security.rememberMe";
   
   private static boolean securityEnabled = true;
   
   public static final String ROLES_GROUP = "Roles";
   
   private static final String LOGIN_TRIED = "org.jboss.seam.security.loginTried";
   
   private static final long serialVersionUID = 3751659008033189259L;
   
   private static final LogProvider log = Logging.getLogProvider(Identity.class);
   
   private String username;
   private String password;
   
   private MethodExpression authenticateMethod;

   private Principal principal;   
   private Subject subject;
   
   private boolean rememberMe;
   
   private String jaasConfigName = null;
   
   private List<String> preAuthenticationRoles = new ArrayList<String>();
   
   private boolean authenticateEveryRequest = false;
   
   /**
    * Flag that indicates we are in the process of authenticating
    */
   private boolean authenticating = false;
      
   @Create
   public void create()
   {     
      subject = new Subject();
      Events.instance().raiseEvent(EVENT_INIT_IDENTITY);
   }
   
   public static boolean isSecurityEnabled()
   {
      return securityEnabled;
   }
   
   public static void setSecurityEnabled(boolean enabled)
   {
      securityEnabled = enabled;
   }
   
   public boolean getAuthenticateEveryRequest()
   {
      return authenticateEveryRequest;
   }
   
   public void setAuthenticateEveryRequest(boolean authenticateEveryRequest)
   {
      this.authenticateEveryRequest = authenticateEveryRequest;
   }

   public void beginRequest() {}
   
   public void endRequest() {}

   public static Identity instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }

      Identity instance = (Identity) Component.getInstance(Identity.class, ScopeType.SESSION);

      if (instance == null)
      {
         throw new IllegalStateException("No Identity could be created");
      }

      return instance;
   }
   
   public boolean isLoggedIn()
   {           
      return isLoggedIn(true);
   }
   
   public boolean isLoggedIn(boolean attemptLogin)
   {
      if (!authenticating && attemptLogin && getPrincipal() == null && isCredentialsSet() &&
          Contexts.isEventContextActive() &&
          !Contexts.getEventContext().isSet(LOGIN_TRIED))
      {
         Contexts.getEventContext().set(LOGIN_TRIED, true);
         quietLogin();
      }     
      
      // If there is a principal set, then the user is logged in.
      return getPrincipal() != null;
   }

   public Principal getPrincipal()
   {
      return principal;
   }
   
   public Subject getSubject()
   {
      return subject;
   }
   
   public boolean isCredentialsSet()
   {
      return username != null && password != null;
   }
      
   /**
    * Performs an authorization check, based on the specified security expression.
    * 
    * @param expr The security expression to evaluate
    * @throws NotLoggedInException Thrown if the authorization check fails and 
    * the user is not authenticated
    * @throws AuthorizationException Thrown if the authorization check fails and
    * the user is authenticated
    */
   public void checkRestriction(String expr)
   {      
      if ( !evaluateExpression(expr) )
      {
         if ( !isLoggedIn() )
         {
            Events.instance().raiseEvent(EVENT_NOT_LOGGED_IN);
            log.debug(String.format(
               "Error evaluating expression [%s] - User not logged in", expr));
            throw new NotLoggedInException();
         }
         else
         {
            Events.instance().raiseEvent(EVENT_NOT_AUTHORIZED);
            throw new AuthorizationException(String.format(
               "Authorization check failed for expression [%s]", expr));
         }
      }
   }

   public String login()
   {
      try
      {
         authenticate();
         if ( log.isDebugEnabled() )
         {
            log.debug("Login successful for: " + getUsername());
         }
         Events.instance().raiseEvent(EVENT_LOGIN_SUCCESSFUL);
         return "loggedIn";
      }
      catch (LoginException ex)
      {
         if ( log.isDebugEnabled() )
         {
             log.debug("Login failed for: " + getUsername(), ex);
         }
         Events.instance().raiseEvent(EVENT_LOGIN_FAILED, ex);
         return null;
      }
   }
   
   /**
    * Attempts a quiet login, suppressing any login exceptions and not creating
    * any faces messages. This method is intended to be used primarily as an 
    * internal API call, however has been made public for convenience.
    */
   public void quietLogin()
   {
      try
      {
         if (isCredentialsSet()) authenticate();
      }
      catch (LoginException ex) { }
   }
   
   public void authenticate() 
      throws LoginException
   {
      // If we're already authenticated, then don't authenticate again
      if (!isLoggedIn(false))
      {
         authenticate( getLoginContext() );
      }
   }

   public void authenticate(LoginContext loginContext) 
      throws LoginException
   {
      try
      {
         authenticating = true;
         preAuthenticate();
         loginContext.login();
         postAuthenticate();         
      }
      finally
      {
         authenticating = false;
      }
   }
   
   protected void preAuthenticate()
   {
      unAuthenticate();
      preAuthenticationRoles.clear();
      Events.instance().raiseEvent(EVENT_PRE_AUTHENTICATE);
   }   
   
   protected void postAuthenticate()
   {
      // Populate the working memory with the user's principals
      for ( Principal p : getSubject().getPrincipals() )
      {         
         if ( !(p instanceof Group))
         {
            if (principal == null) 
            {
               principal = p;
               break;
            }            
         }         
      }      
      
      if (!preAuthenticationRoles.isEmpty() && isLoggedIn())
      {
         for (String role : preAuthenticationRoles)
         {
            addRole(role);
         }
         preAuthenticationRoles.clear();
      }
      
      password = null;

      Events.instance().raiseEvent(EVENT_POST_AUTHENTICATE);
   }
   
   /**
    * Removes all Role objects from the security context, removes the "Roles"
    * group from the user's subject.
    *
    */
   protected void unAuthenticate()
   {      
      principal = null;
      
      for ( Group sg : getSubject().getPrincipals(Group.class) )      
      {
         if ( ROLES_GROUP.equals( sg.getName() ) )
         {
            getSubject().getPrincipals().remove(sg);
            break;
         }
      }
   }

   protected LoginContext getLoginContext() throws LoginException
   {
      if (getJaasConfigName() != null)
      {
         return new LoginContext(getJaasConfigName(), getSubject(), 
                  getDefaultCallbackHandler());
      }
      
      return new LoginContext(Configuration.DEFAULT_JAAS_CONFIG_NAME, 
               getSubject(), getDefaultCallbackHandler(), Configuration.instance());
   }
   
   public void logout()
   {
      principal = null;
      unAuthenticate();
      Session.instance().invalidate();
      Events.instance().raiseEvent(EVENT_LOGGED_OUT);
   }

   /**
    * Checks if the authenticated Identity is a member of the specified role.
    * 
    * @param role String The name of the role to check
    * @return boolean True if the user is a member of the specified role
    */
   public boolean hasRole(String role)
   {
      isLoggedIn(true);
      
      for ( Group sg : getSubject().getPrincipals(Group.class) )      
      {
         if ( ROLES_GROUP.equals( sg.getName() ) )
         {
            return sg.isMember( new SimplePrincipal(role) );
         }
      }
      return false;
   }
   
   /**
    * Adds a role to the user's subject, and their security context
    * 
    * @param role The name of the role to add
    */
   public boolean addRole(String role)
   {
      if (role == null || "".equals(role)) return false;
      
      if (!isLoggedIn())
      {
         preAuthenticationRoles.add(role);
         return false;
      }
      else
      {
         for ( Group sg : getSubject().getPrincipals(Group.class) )      
         {
            if ( ROLES_GROUP.equals( sg.getName() ) )
            {
               return sg.addMember(new SimplePrincipal(role));
            }
         }
                  
         SimpleGroup roleGroup = new SimpleGroup(ROLES_GROUP);
         roleGroup.addMember(new SimplePrincipal(role));
         getSubject().getPrincipals().add(roleGroup);
         return true;
      }
   }

   /**
    * Removes a role from the user's subject and their security context
    * 
    * @param role The name of the role to remove
    */
   public void removeRole(String role)
   {     
      for ( Group sg : getSubject().getPrincipals(Group.class) )      
      {
         if ( ROLES_GROUP.equals( sg.getName() ) )
         {
            Enumeration e = sg.members();
            while (e.hasMoreElements())
            {
               Principal member = (Principal) e.nextElement();
               if (member.getName().equals(role))
               {
                  sg.removeMember(member);
                  break;
               }
            }

         }
      }      
   }   
   
   /**
    * Assert that the current authenticated Identity is a member of
    * the specified role.
    * 
    * @param role String The name of the role to check
    * @throws AuthorizationException if not a member
    */
   public void checkRole(String role)
   {
      isLoggedIn(true);
      
      if ( !hasRole(role) )
      {
         if ( !isLoggedIn() )
         {
            Events.instance().raiseEvent(EVENT_NOT_LOGGED_IN);
            throw new NotLoggedInException();
         }
         else
         {
            throw new AuthorizationException(String.format(
                  "Authorization check failed for role [%s]", role));
         }
      }
   }

   /**
    * Assert that the current authenticated Identity has permission for
    * the specified name and action
    * 
    * @param name String The permission name
    * @param action String The permission action
    * @param arg Object Optional object parameter used to make a permission decision
    * @throws AuthorizationException if the user does not have the specified permission
    */
   public void checkPermission(String name, String action, Object...arg)
   {
      isLoggedIn(true);
      
      if ( !hasPermission(name, action, arg) )
      {
         if ( !isLoggedIn() )
         {
            Events.instance().raiseEvent(EVENT_NOT_LOGGED_IN);
            throw new NotLoggedInException();
         }
         else
         {
            throw new AuthorizationException(String.format(
                  "Authorization check failed for permission [%s,%s]", name, action));
         }
      }
   }

   /**
    * Performs a permission check for the specified name and action
    * 
    * @param name String The permission name
    * @param action String The permission action
    * @param arg Object Optional object parameter used to make a permission decision
    * @return boolean True if the user has the specified permission
    */
   public boolean hasPermission(String name, String action, Object...arg)
   {      
      return false;
   }   
   
   /**
    * Creates a callback handler that can handle a standard username/password
    * callback, using the username and password properties.
    */
   protected CallbackHandler getDefaultCallbackHandler()
   {
      return new CallbackHandler() 
      {
         public void handle(Callback[] callbacks) 
            throws IOException, UnsupportedCallbackException 
         {
            for (int i=0; i<callbacks.length; i++)
            {
               if (callbacks[i] instanceof NameCallback)
               {
                  ( (NameCallback) callbacks[i] ).setName(getUsername());
               }
               else if (callbacks[i] instanceof PasswordCallback)
               {
                  ( (PasswordCallback) callbacks[i] ).setPassword( getPassword() != null ? 
                           getPassword().toCharArray() : null );
               }
               else
               {
                  throw new UnsupportedCallbackException(callbacks[i], "Unsupported callback");
               }
            }
            
         }
      };
   }
   
   /**
    * Evaluates the specified security expression, which must return a boolean
    * value.
    * 
    * @param expr String The expression to evaluate
    * @return boolean The result of the expression evaluation
    */
   protected boolean evaluateExpression(String expr) 
   {    
      // The following line doesn't work in MyFaces      
      return Expressions.instance().createValueExpression(expr, Boolean.class).getValue();
   }   
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {  
      if (this.username != username && (this.username == null || !this.username.equals(username)))
      {
         this.username = username;
         Events.instance().raiseEvent(EVENT_CREDENTIALS_UPDATED);
      }
   }
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      if (this.password != password && (this.password == null || !this.password.equals(password)))
      {
         this.password = password;
         Events.instance().raiseEvent(EVENT_CREDENTIALS_UPDATED);
      }      
   }
   
   public MethodExpression getAuthenticateMethod()
   {
      return authenticateMethod;
   }
   
   public void setAuthenticateMethod(MethodExpression authMethod)
   {
      this.authenticateMethod = authMethod;
   }
   
   public boolean isRememberMe()
   {
      return rememberMe;
   }
   
   public void setRememberMe(boolean remember)
   {
      if (this.rememberMe != remember)
      {
         this.rememberMe = remember;
         Events.instance().raiseEvent(EVENT_REMEMBER_ME);
      }
   }
   
   public String getJaasConfigName()
   {
      return jaasConfigName;
   }
   
   public void setJaasConfigName(String jaasConfigName)
   {
      this.jaasConfigName = jaasConfigName;
   }

   public void checkEntityPermission(Object entity, EntityAction action)
   {      
      isLoggedIn(true);
      
      PersistenceProvider provider = PersistenceProvider.instance(); 
      Class beanClass = provider.getBeanClass(entity);
      
      if (beanClass != null)
      {
         String name = Seam.getComponentName(entity.getClass());
         if (name == null) name = beanClass.getName();  
         
         Method m = null;
         switch (action)
         {
            case READ:
               m = provider.getPostLoadMethod(beanClass);
               break;
            case INSERT:
               m = provider.getPrePersistMethod(beanClass);
               break;
            case UPDATE:
               m = provider.getPreUpdateMethod(beanClass);
               break;
            case DELETE:
               m = provider.getPreRemoveMethod(beanClass);
         }
         
         Restrict restrict = null;
         
         if (m != null && m.isAnnotationPresent(Restrict.class))
         {
            restrict = m.getAnnotation(Restrict.class);
         }
         else if (entity.getClass().isAnnotationPresent(Restrict.class))
         {
            restrict = entity.getClass().getAnnotation(Restrict.class);
         }

         if (restrict != null)
         {
            if (Strings.isEmpty(restrict.value()))
            {
               checkPermission(name, action.toString(), entity);
            }
            else
            {
               checkRestriction(restrict.value());
            }
         }
      }
   }   
}
