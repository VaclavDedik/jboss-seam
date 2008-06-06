package org.jboss.seam.security;

import static org.jboss.seam.ScopeType.SESSION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.rmi.server.UID;
import java.util.Random;

import javax.faces.context.FacesContext;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.intercept.BypassInterceptors;
import org.jboss.seam.faces.Selector;
import org.jboss.seam.security.management.IdentityManager;
import org.jboss.seam.util.Base64;

/**
 * Remember-me functionality is provided by this class, in two different flavours.  The first mode
 * provides username-only persistence, and is considered to be secure as the user (or their browser)
 * is still required to provide a password.  The second mode provides an auto-login feature, however
 * is NOT considered to be secure and is vulnerable to XSS attacks compromising the user's account.
 * 
 * Use the auto-login mode with caution!
 * 
 * @author Shane Bryzak
 */
@Name("org.jboss.seam.security.rememberMe")
@Scope(SESSION)
@Install(precedence = BUILT_IN, classDependencies = "javax.faces.context.FacesContext")
@BypassInterceptors
public class RememberMe
{
   class UsernameSelector extends Selector
   {
      @Override
      public String getCookieName()
      {
         return "org.jboss.seam.security.username";
      }       
      
      @Override
      public void setDirty()
      {
         super.setDirty();
      }
      
      @Override
      public String getCookieValue()
      {
         return super.getCookieValue();
      }
      
      @Override
      public void clearCookieValue()
      {
         super.clearCookieValue();
      }
      
      @Override
      public void setCookieValueIfEnabled(String value)
      {
         super.setCookieValueIfEnabled(value);
      }
   }
   
   class TokenSelector extends UsernameSelector
   {
      @Override
      public String getCookieName()
      {
         return "org.jboss.seam.security.token";
      }
   }
   
   private class DecodedToken
   {
      private String username;
      private String value;
      
      public DecodedToken(String cookieValue)
      {
         String decoded = new String(Base64.decode(cookieValue));
         
         username = decoded.substring(0, decoded.indexOf(':'));
         value = decoded.substring(decoded.indexOf(':') + 1);                  
      }
      
      public String getUsername()
      {
         return username;
      }
      
      public String getValue()
      {
         return value;
      }
   }
      
   private UsernameSelector usernameSelector;
   
   private TokenSelector tokenSelector;   
   private TokenStore tokenStore;
      
   private boolean enabled;
   
   private boolean autoLoggedIn;
   
   private Random random = new Random(System.currentTimeMillis());
   
   public enum Mode { disabled, usernameOnly, autoLogin}
   
   private Mode mode = Mode.usernameOnly;
   
   public Mode getMode()
   {
      return mode;
   }
   
   public void setMode(Mode mode)
   {
      this.mode = mode;
   }
   
   public boolean isEnabled()
   {
      return enabled;
   }
   
   public void setEnabled(boolean enabled)
   {
      if (this.enabled != enabled)
      {
         this.enabled = enabled;
         usernameSelector.setCookieEnabled(enabled);
         usernameSelector.setDirty();
      }      
   }
   
   public TokenStore getTokenStore()
   {
      return tokenStore;
   }
   
   public void setTokenStore(TokenStore tokenStore)
   {
      this.tokenStore = tokenStore;
   }
   
   @Create
   public void create()
   {
      if (mode.equals(Mode.usernameOnly))
      {      
         usernameSelector = new UsernameSelector();
      }
      else if (mode.equals(Mode.autoLogin))
      {
         tokenSelector = new TokenSelector();

         // Default to JpaTokenStore
         if (tokenStore == null)
         {
            tokenStore = (TokenStore) Component.getInstance(JpaTokenStore.class, true);
         }         
      }
   }
   
   protected String generateTokenValue()
   {
      StringBuilder sb = new StringBuilder();
      sb.append(new UID().toString());
      sb.append(":");
      sb.append(random.nextLong());
      return sb.toString();
   }
   
   protected String encodeToken(String username, String value)
   {
      StringBuilder sb = new StringBuilder();
      sb.append(username);
      sb.append(":");
      sb.append(value);
      return Base64.encodeBytes(sb.toString().getBytes());      
   }
   
   @Observer(Credentials.EVENT_INIT_CREDENTIALS)
   public void initCredentials(Credentials credentials)
   {             
      if (mode.equals(Mode.usernameOnly))
      {
         FacesContext ctx = FacesContext.getCurrentInstance();
         if (ctx != null)
         {
            usernameSelector.setCookiePath(ctx.getExternalContext().getRequestContextPath());
         }
         
         String username = usernameSelector.getCookieValue();
         if (username!=null)
         {
            setEnabled(true);
            credentials.setUsername(username);
         }
               
         usernameSelector.setDirty();
      }
      else if (mode.equals(Mode.autoLogin))
      {
         FacesContext ctx = FacesContext.getCurrentInstance();
         if (ctx != null)
         {
            tokenSelector.setCookiePath(ctx.getExternalContext().getRequestContextPath());
         }
         
         String token = usernameSelector.getCookieValue();
         if (token != null)
         {
            setEnabled(true);
            
            DecodedToken decoded = new DecodedToken(token);

            if (tokenStore.validateToken(decoded.getUsername(), decoded.getValue()))
            {
               credentials.setUsername(decoded.getUsername());
               credentials.setPassword(decoded.getValue());               
            }
            else
            {
               // Have we been compromised? Just in case, invalidate all authentication tokens
               tokenStore.invalidateAll(decoded.getUsername());
            }
         }
      }
   }
   
   @Observer(Identity.EVENT_QUIET_LOGIN)
   public void quietLogin(Identity identity)
   {
      if (mode.equals(Mode.autoLogin) && isEnabled())
      {
         // Double check our credentials again
         if (tokenStore.validateToken(identity.getCredentials().getUsername(), 
               identity.getCredentials().getPassword()))
         {
            // Success, authenticate the user
            identity.getSubject().getPrincipals().add(new SimplePrincipal(
                  identity.getCredentials().getUsername()));            
            // And populate the roles
            for (String role : IdentityManager.instance().getImpliedRoles(
                  identity.getCredentials().getUsername()))
            {
               identity.addRole(role);
            }
            
            identity.postAuthenticate();
            
            autoLoggedIn = true;
         }
      }
   }
   
   @Observer(Identity.EVENT_LOGGED_OUT)
   public void loggedOut()
   {
      if (mode.equals(Mode.autoLogin))
      {
         tokenSelector.getCookieValue();
      }
   }
   
   @Observer(Identity.EVENT_POST_AUTHENTICATE)
   public void postAuthenticate(Identity identity)
   {
      if (mode.equals(Mode.usernameOnly))
      {
         // Password is set to null during authentication, so we set dirty
         usernameSelector.setDirty();
               
         if ( !enabled ) usernameSelector.clearCookieValue();
         usernameSelector.setCookieValueIfEnabled( Identity.instance().getCredentials().getUsername() );
      }
      else if (mode.equals(Mode.autoLogin))
      {
         tokenSelector.setDirty();
         
         DecodedToken decoded = new DecodedToken(tokenSelector.getCookieValue());
         
         // Invalidate the current token whether enabled or not
         tokenStore.invalidateToken(decoded.getUsername(), decoded.getValue());
         
         if ( !enabled ) 
         {
            tokenSelector.clearCookieValue();         
         }
         else
         {
            String value = generateTokenValue();
            tokenStore.createToken(decoded.getUsername(), value);
            tokenSelector.setCookieValueIfEnabled(encodeToken(decoded.getUsername(), value));            
         }
      }
   }        
   
   @Observer(Credentials.EVENT_CREDENTIALS_UPDATED)
   public void credentialsUpdated()
   {
      usernameSelector.setDirty();
   }      
   
   /**
    * A flag that an application can use to protect sensitive operations if the user has been
    * auto-authenticated. 
    */
   public boolean isAutoLoggedIn()
   {
      return autoLoggedIn;
   }
}