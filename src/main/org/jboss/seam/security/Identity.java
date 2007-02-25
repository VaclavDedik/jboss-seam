package org.jboss.seam.security;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.Component;
import org.jboss.seam.Entity;
import org.jboss.seam.Model;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.security.Restrict;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Selector;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.Strings;
import org.jboss.seam.util.UnifiedELValueBinding;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@Startup
public class Identity extends Selector
{  
   public static final String ROLES_GROUP = "Roles";
   
   private static final long serialVersionUID = 3751659008033189259L;
   
   private static final LogProvider log = Logging.getLogProvider(Identity.class);
   
   private String username;
   private String password;
   
   private MethodBinding authenticateMethod;

   private Principal principal;   
   private Subject subject;
   
   private String jaasConfigName = null;
   
   private List<String> preAuthenticationRoles = new ArrayList<String>();
   
   @Override
   protected String getCookieName()
   {
      return "org.jboss.seam.security.username";
   }
      
   @Create
   public void create()
   {     
      subject = new Subject();
      initCredentialsFromCookie();
   }

   private void initCredentialsFromCookie()
   {
      boolean cookie = isCookieEnabled();
      setCookieEnabled(true);
      username = getCookieValue();
      setCookieEnabled(cookie);
      if (username!=null)
      {
         postRememberMe();
      }
      setDirty();
   }

   protected void postRememberMe()
   {
      Events.instance().raiseEvent("org.jboss.seam.rememberMe");
   }

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
   
   /**
    * If there is a principal set, then the user is logged in.
    * 
    */
   public boolean isLoggedIn()
   {
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
            Events.instance().raiseEvent("org.jboss.seam.notLoggedIn");
            log.debug(String.format(
               "Error evaluating expression [%s] - User not logged in", expr));
            throw new NotLoggedInException();
         }
         else
         {
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
         addLoginSuccessfulMessage();
         return "loggedIn";
      }
      catch (LoginException ex)
      {
         if ( log.isDebugEnabled() )
         {
             log.debug("Login failed for: " + getUsername(), ex);
         }
         addLoginFailedMessage(ex);
         return null;
      }
   }

   protected void addLoginFailedMessage(LoginException ex)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
               getLoginFailedMessageSeverity(), 
               getLoginFailedMessageKey(), 
               getLoginFailedMessage(), 
               ex);
   }

   protected String getLoginFailedMessage()
   {
      return "Login failed";
   }

   protected Severity getLoginFailedMessageSeverity()
   {
      return FacesMessage.SEVERITY_INFO;
   }

   protected String getLoginFailedMessageKey()
   {
      return "org.jboss.seam.loginFailed";
   }

   protected void addLoginSuccessfulMessage()
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
               getLoginSuccessfulMessageSeverity(), 
               getLoginSuccessfulMessageKey(), 
               getLoginSuccessfulMessage(), 
               getUsername());
   }

   protected Severity getLoginSuccessfulMessageSeverity()
   {
      return FacesMessage.SEVERITY_INFO;
   }

   protected String getLoginSuccessfulMessage()
   {
      return "Welcome, #0";
   }

   protected String getLoginSuccessfulMessageKey()
   {
      return "org.jboss.seam.loginSuccessful";
   }
   
   public void authenticate() 
      throws LoginException
   {
      authenticate( getLoginContext() );
   }

   public void authenticate(LoginContext loginContext) 
      throws LoginException
   {
      preAuthenticate();
      loginContext.login();
      postAuthenticate();
   }
   
   protected void preAuthenticate()
   {
      preAuthenticationRoles.clear();
      Events.instance().raiseEvent("org.jboss.seam.preAuthenticate");
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
               setDirty();
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
      
      setCookieValue( getUsername() );
      
      password = null;
      setDirty();

      Events.instance().raiseEvent("org.jboss.seam.postAuthenticate");
   }
   
   /**
    * Removes all Role objects from the security context, removes the "Roles"
    * group from the user's subject.
    *
    */
   protected void unAuthenticate()
   {      
      for ( Group sg : subject.getPrincipals(Group.class) )      
      {
         if ( ROLES_GROUP.equals( sg.getName() ) )
         {
            subject.getPrincipals().remove(sg);
            break;
         }
      }
   }

   protected LoginContext getLoginContext() throws LoginException
   {
      if (getJaasConfigName() != null)
      {
         return new LoginContext(getJaasConfigName(), subject, 
                  getDefaultCallbackHandler());
      }
      
      return new LoginContext(Configuration.DEFAULT_JAAS_CONFIG_NAME, 
            subject, getDefaultCallbackHandler(), Configuration.instance());
   }
   
   public void logout()
   {
      principal = null;
      Seam.invalidateSession();
   }

   /**
    * Checks if the authenticated Identity is a member of the specified role.
    * 
    * @param role String The name of the role to check
    * @return boolean True if the user is a member of the specified role
    */
   public boolean hasRole(String role)
   {
      for ( Group sg : subject.getPrincipals(Group.class) )      
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
      if (!isLoggedIn())
      {
         preAuthenticationRoles.add(role);
         return false;
      }
      else
      {
         for ( Group sg : subject.getPrincipals(Group.class) )      
         {
            if ( ROLES_GROUP.equals( sg.getName() ) )
            {
               return sg.addMember(new SimplePrincipal(role));
            }
         }
                  
         SimpleGroup roleGroup = new SimpleGroup(ROLES_GROUP);
         roleGroup.addMember(new SimplePrincipal(role));
         subject.getPrincipals().add(roleGroup);
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
      for ( Group sg : subject.getPrincipals(Group.class) )      
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
      if ( !hasRole(role) )
      {
         if ( !isLoggedIn() )
         {
            Events.instance().raiseEvent("org.jboss.seam.notLoggedIn");
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
      if ( !hasPermission(name, action, arg) )
      {
         if ( !isLoggedIn() )
         {
            Events.instance().raiseEvent("org.jboss.seam.notLoggedIn");
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
      // The following line doesn't work because of a bug in MyFaces      
      //return (Boolean) Expressions.instance().createValueBinding(expr).getValue();

      return (Boolean) new UnifiedELValueBinding(expr).getValue( FacesContext.getCurrentInstance() );
   }   
   
   public String getUsername()
   {
      return username;
   }
   
   public void setUsername(String username)
   {
      setDirty(this.username, username);
      this.username = username;
   }
   
   public String getPassword()
   {
      return password;
   }
   
   public void setPassword(String password)
   {
      setDirty(this.password, password);
      this.password = password;
   }
   
   public MethodBinding getAuthenticateMethod()
   {
      return authenticateMethod;
   }
   
   public void setAuthenticateMethod(MethodBinding authMethod)
   {
      this.authenticateMethod = authMethod;
   }
   
   public boolean isRememberMe()
   {
      return isCookieEnabled();
   }
   
   public void setRememberMe(boolean remember)
   {
      setCookieEnabled(remember);
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
      Entity e = (Entity) Model.forClass(entity.getClass());
      
      if (e != null)
      {
         String name = Seam.getComponentName(entity.getClass());
         if (name == null) name = entity.getClass().getName();  
         
         Method m = null;
         switch (action)
         {
            case READ:
               m = e.getPostLoadMethod();
               break;
            case INSERT:
               m = e.getPrePersistMethod();
               break;
            case UPDATE:
               m = e.getPreUpdateMethod();
               break;
            case DELETE:
               m = e.getPreRemoveMethod();
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
