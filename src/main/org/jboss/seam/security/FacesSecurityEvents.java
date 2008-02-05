package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;
import javax.security.auth.login.LoginException;

import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.faces.Selector;

/**
 * Produces FacesMessages for certain security events, and decouples the
 * Identity component from JSF - and also handles cookie functionality.
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.facesSecurityEvents")
@Scope(APPLICATION)
@Install(precedence = BUILT_IN, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
@Startup
public class FacesSecurityEvents extends Selector
{  
   @Override
   public String getCookieName()
   {
      return "org.jboss.seam.security.username";
   }   
   
   @Observer("org.jboss.seam.postCreate.org.jboss.seam.security.identity")
   public void initCredentialsFromCookie(Identity identity)
   {       
      FacesContext ctx = FacesContext.getCurrentInstance();
      if (ctx != null)
      {
         setCookiePath(ctx.getExternalContext().getRequestContextPath());
      }
      
      identity.setRememberMe(isCookieEnabled());      
      
      String username = getCookieValue();
      if (username!=null)
      {
         setCookieEnabled(true);
         identity.setUsername(username);
         postRememberMe(identity);
      }
            
      setDirty();
   }
   
   @Observer(Identity.EVENT_CREDENTIALS_UPDATED)
   public void credentialsUpdated()
   {
      setDirty();
   }
   
   @Observer(Identity.EVENT_POST_AUTHENTICATE)
   public void postAuthenticate(Identity identity)
   {
      // Password is set to null during authentication, so we set dirty
      setDirty();
            
      if ( !identity.isRememberMe() ) clearCookieValue();
      setCookieValueIfEnabled( identity.getUsername() );      
   }
   
   @Observer(Identity.EVENT_REMEMBER_ME)
   public void postRememberMe(Identity identity)
   {
      setCookieEnabled(identity.isRememberMe());
   }     
   
   @Observer(Identity.EVENT_LOGIN_FAILED)
   public void addLoginFailedMessage(LoginException ex)
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
               getLoginFailedMessageSeverity(), 
               getLoginFailedMessageKey(), 
               getLoginFailedMessage(), 
               ex);
   }

   public String getLoginFailedMessage()
   {
      return "Login failed";
   }

   public Severity getLoginFailedMessageSeverity()
   {
      return FacesMessage.SEVERITY_INFO;
   }

   public String getLoginFailedMessageKey()
   {
      return "org.jboss.seam.loginFailed";
   }

   @Observer(Identity.EVENT_LOGIN_SUCCESSFUL)
   public void addLoginSuccessfulMessage()
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
               getLoginSuccessfulMessageSeverity(), 
               getLoginSuccessfulMessageKey(), 
               getLoginSuccessfulMessage(), 
               Identity.instance().getUsername());
   }
   
   @Observer(Identity.EVENT_NOT_LOGGED_IN)
   public void addNotLoggedInMessage()
   {      
      FacesMessages.instance().addFromResourceBundleOrDefault( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NotLoggedIn", 
            "Please log in first" 
         );      
   }

   public Severity getLoginSuccessfulMessageSeverity()
   {
      return FacesMessage.SEVERITY_INFO;
   }

   public String getLoginSuccessfulMessage()
   {
      return "Welcome, #0";
   }

   public String getLoginSuccessfulMessageKey()
   {
      return "org.jboss.seam.loginSuccessful";
   }   
   
   @Observer(Identity.EVENT_ALREADY_LOGGED_IN)
   public void addAlreadyLoggedInMessage()
   {
      FacesMessages.instance().addFromResourceBundleOrDefault (
         FacesMessage.SEVERITY_WARN,
         "org.jboss.seam.AlreadyLoggedIn",
         "You are already logged in, please log out first if you wish to log in again"
      );
   }
}
