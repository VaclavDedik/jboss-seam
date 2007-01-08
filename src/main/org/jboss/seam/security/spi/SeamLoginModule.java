package org.jboss.seam.security.spi;

import java.security.acl.Group;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.jboss.seam.core.Expressions;
import org.jboss.seam.core.Expressions.MethodBinding;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.security.SimpleGroup;
import org.jboss.seam.security.SimplePrincipal;

/**
 * Performs authentication using a Seam component
 * 
 * @author Shane Bryzak
 */
public class SeamLoginModule implements LoginModule
{
   private static final String OPTS_LOGIN_METHOD = "loginMethod";
   
   private static final LogProvider log = Logging.getLogProvider(SeamLoginModule.class);   
   
   private Set<String> roles = new HashSet<String>();
   private Subject subject;
   private Map<String,?> options;
   private CallbackHandler callbackHandler;
   
   private String username;
   
   public boolean abort() throws LoginException
   {
      return true;
   }

   public boolean commit() throws LoginException
   {        
      subject.getPrincipals().add(new SimplePrincipal(username));
      
      Group roleGroup = new SimpleGroup("roles");
      for (String role : roles)
      {
         roleGroup.addMember(new SimplePrincipal(role));
      }
      
      subject.getPrincipals().add(roleGroup);
      
      return true;
   }

   public void initialize(Subject subject, CallbackHandler callbackHandler,
         Map<String, ?> sharedState, Map<String, ?> options)
   {
      this.subject = subject;
      this.options = options;
      this.callbackHandler = callbackHandler;
   }

   public boolean login() 
      throws LoginException
   {
      MethodBinding mb = Expressions.instance().createMethodBinding(
            (String) options.get(OPTS_LOGIN_METHOD));
      
      Object[] params = null;
      
      try
      {
         params = getLoginParams();
      }
      catch (Exception e)
      {         
         log.error("Error logging in", e);         
         throw new LoginException(e.getMessage());
      }
      
      try
      {
        return (Boolean) mb.invoke(getLoginParamTypes(), params);      
      }
      catch (RuntimeException ex)
      {
         log.error("Error invoking login method", ex);
         return false;
      }
   }
   
   public Class[] getLoginParamTypes()
   {
      return new Class[] {String.class, String.class, Set.class };
   }
   
   /**
    * Override this method if this isn't a standard username/password-based
    * authentication.
    * 
    * @return
    * @throws Exception
    */
   public Object[] getLoginParams()
      throws Exception
   {
      NameCallback cbName = new NameCallback("Enter username");
      PasswordCallback cbPassword = new PasswordCallback("Enter password", false);

      // Get the username and password from the callback handler
      callbackHandler.handle(new Callback[] { cbName, cbPassword });
      username = cbName.getName();
      
      return new Object[] { username, new String(cbPassword.getPassword()), 
            roles };
   }

   public boolean logout() throws LoginException
   {
      
      return true;
   }
}
