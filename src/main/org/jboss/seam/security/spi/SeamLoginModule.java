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
import org.jboss.seam.util.Reflections;

/**
 * Performs authentication using a Seam component
 * 
 * @author Shane Bryzak
 */
public class SeamLoginModule implements LoginModule
{
   private static final String OPTS_AUTH_METHOD = "authMethod";
   private static final String OPTS_PARAM_TYPES = "paramTypes";
   
   private static final LogProvider log = Logging.getLogProvider(SeamLoginModule.class);   
   
   protected Set<String> roles = new HashSet<String>();
   
   protected Subject subject;
   protected Map<String,?> options;
   protected CallbackHandler callbackHandler;
   
   protected String username;
   
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
            (String) options.get(OPTS_AUTH_METHOD));
      
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
      catch (ClassNotFoundException ex)
      {
         log.error("Error determining parameter types", ex);
         return false;
      }
   }
   
   /**
    * Returns the authentication method param types as a Class array.
    * 
    * @throws ClassNotFoundException
    */
   public Class[] getLoginParamTypes()
      throws ClassNotFoundException
   {
      if (!options.containsKey(OPTS_PARAM_TYPES))
         return new Class[] {String.class, String.class, Set.class };

      String[] paramTypes = ((String) options.get(OPTS_PARAM_TYPES)).split("[,]");
      Class[] types = new Class[paramTypes.length];
      for (int i = 0; i < paramTypes.length; i++)
         types[i] = Reflections.classForName(paramTypes[i].trim());
            
      return types;
   }
   
   /**
    * Override this method if this isn't a standard username/password-based
    * authentication.
    * 
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
