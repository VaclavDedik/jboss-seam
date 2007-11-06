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
   
   @Observer("org.jboss.seam.security.initIdentity")
   public void initCredentialsFromCookie()
   {       
      FacesContext ctx = FacesContext.getCurrentInstance();
      if (ctx != null)
      {
         setCookiePath(ctx.getExternalContext().getRequestContextPath());
      }
      
      Identity.instance().setRememberMe(isCookieEnabled());      
      
      String username = getCookieValue();
      if (username!=null)
      {
         setCookieEnabled(true);
         Identity.instance().setUsername(username);
         postRememberMe();
      }
            
      setDirty();
   }
   
   @Observer("org.jboss.seam.security.credentialsUpdated")
   public void credentialsUpdated()
   {
      setDirty();
   }
   
   @Observer("org.jboss.seam.postAuthenticate")
   public void postAuthenticate()
   {
      // Password is set to null during authentication, so we set dirty
      setDirty();
            
      if ( !Identity.instance().isRememberMe() ) clearCookieValue();
      setCookieValueIfEnabled( Identity.instance().getUsername() );      
   }
   
   @Observer("org.jboss.seam.security.rememberMe")
   public void postRememberMe()
   {
      setCookieEnabled(Identity.instance().isRememberMe());
   }     
   
   @Observer("org.jboss.seam.security.loginFailed")
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

   @Observer("org.jboss.seam.security.loginSuccessful")
   public void addLoginSuccessfulMessage()
   {
      FacesMessages.instance().addFromResourceBundleOrDefault(
               getLoginSuccessfulMessageSeverity(), 
               getLoginSuccessfulMessageKey(), 
               getLoginSuccessfulMessage(), 
               Identity.instance().getUsername());
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
}
