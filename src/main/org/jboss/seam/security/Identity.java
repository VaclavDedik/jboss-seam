package org.jboss.seam.security;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Selector;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.UnifiedELValueBinding;

@Name("org.jboss.seam.security.identity")
@Scope(SESSION)
@Install(precedence = BUILT_IN, classDependencies="org.drools.WorkingMemory")
@Intercept(NEVER)
@Startup
public class Identity extends Selector
{  
   private static final long serialVersionUID = 3751659008033189259L;
   
   private static final LogProvider log = Logging.getLogProvider(Identity.class);
      
   private String username;
   private String password;
   
   private MethodBinding authenticateMethod;

   private Principal principal;   
   private Subject subject;
   
   private WorkingMemory securityContext;
   
   private String jaasConfigName = null;
   
   @Override
   protected String getCookieName()
   {
      return "org.jboss.seam.security.username";
   }
      
   @Create
   public void create()
   {     
      subject = new Subject();
      initSecurityContext();
      initCredentialsFromCookie();
   }

   private void initCredentialsFromCookie()
   {
      setCookieEnabled(true);
      username = getCookieValue();
      setDirty();
      setCookieEnabled(false);
      if (username!=null)
      {
         postRememberMe();
      }
   }

   protected void postRememberMe()
   {
      Events.instance().raiseEvent("org.jboss.seam.rememberMe");
   }
   
   protected void initSecurityContext()
   {
      if (securityContext==null) //it might have been configured via components.xml
      {
         RuleBase securityRules = (RuleBase) Component.getInstance("securityRules", true);
         if (securityRules != null)
         {
            securityContext = securityRules.newWorkingMemory(false);
            setDirty();
         }
      }
   }

   public static Identity instance()
   {
      if ( !Contexts.isSessionContextActive() )
      {
         throw new IllegalStateException("No active session context");
      }

      Identity instance = (Identity) Component.getInstance(Identity.class, ScopeType.SESSION, true);

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
         log.debug("Login successful for: #0" + getUsername());
         FacesMessages.instance().addFromResourceBundle(FacesMessage.SEVERITY_INFO, "org.jboss.seam.loginSuccessful", "Welcome, #0", getUsername());
         return "success";
      }
      catch (LoginException ex)
      {
         log.debug("Login failed for:" + getUsername(), ex);
         FacesMessages.instance().addFromResourceBundle(FacesMessage.SEVERITY_INFO, "org.jboss.seam.loginFailed", "Login failed", ex);
         return null;
      }
   }
   
   public void authenticate() throws LoginException
   {
      authenticate( getLoginContext() );
   }

   public void authenticate(LoginContext loginContext) throws LoginException
   {
      preAuthenticate();
      loginContext.login();
      postAuthenticate();
   }
   
   protected void preAuthenticate()
   {
      Events.instance().raiseEvent("org.jboss.seam.preAuthenticate");
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
         if ( "roles".equals( sg.getName() ) )
         {
            return sg.isMember( new SimplePrincipal(role) );
         }
      }
      return false;
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
         throw new AuthorizationException(String.format(
                  "Authorization check failed for role [%s]", role));
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
         throw new AuthorizationException(String.format(
                  "Authorization check failed for permission [%s,%s]", name, action));
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
      List<FactHandle> handles = new ArrayList<FactHandle>();

      PermissionCheck check = new PermissionCheck(name, action);

      WorkingMemory securityContext = getSecurityContext();
      assertSecurityContextExists();
      synchronized( securityContext )
      {
         handles.add( securityContext.assertObject(check) );
         
         for (int i = 0; i < arg.length; i++)
         {
            if (i == 0 && arg[0] instanceof Collection)
            {
               for (Object value : (Collection) arg[i])
               {
                  if ( securityContext.getFactHandle(value) == null )
                  {
                     handles.add( securityContext.assertObject(value) );
                  }
               }               
            }
            else
            {
               handles.add( securityContext.assertObject(arg[i]) );
            }
         }
   
         securityContext.fireAllRules();
   
         for (FactHandle handle : handles)
            securityContext.retractObject(handle);
      }
      
      return check.isGranted();
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
    * Populates the specified subject's roles with any inherited roles
    * according to the role memberships contained within the current 
    * SecurityConfiguration
    */
   protected void postAuthenticate()
   {
      populateSecurityContext();
      
      setCookieValue( getUsername() );
      
      password = null;
      setDirty();

      Events.instance().raiseEvent("org.jboss.seam.postAuthenticate");

   }

   protected void populateSecurityContext()
   {
      WorkingMemory securityContext = getSecurityContext();
      assertSecurityContextExists();

      // Populate the working memory with the user's principals
      for ( Principal p : getSubject().getPrincipals() )
      {         
         if ( (p instanceof Group) && "roles".equals( ( (Group) p ).getName() ) )
         {
            Enumeration e = ( (Group) p ).members();
            while ( e.hasMoreElements() )
            {
               Principal role = (Principal) e.nextElement();
               securityContext.assertObject( new Role( role.getName() ) );
            }
         }
         else
         {
            if (principal == null) 
            {
               principal = p;
               setDirty();
            }
            securityContext.assertObject(p);            
         }
         
      }
   }

   private void assertSecurityContextExists()
   {
      if (securityContext==null)
      {
         throw new IllegalStateException("no security rule base available - please install a RuleBase with the name 'securityContext'");
      }
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
   
   public WorkingMemory getSecurityContext()
   {
      return securityContext;
   }
   
   public void setSecurityContext(WorkingMemory securityContext)
   {
      this.securityContext = securityContext;
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
}
