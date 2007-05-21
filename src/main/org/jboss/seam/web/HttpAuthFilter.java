package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.jboss.seam.util.Base64;

@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.web.httpAuthFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@Filter(within = "org.jboss.seam.web.exceptionFilter")
public class HttpAuthFilter extends AbstractFilter
{
   private static final String DEFAULT_REALM = "seamApp";
   
   public enum AuthType {basic, digest}
   
   private String realmName = DEFAULT_REALM;
   
   private AuthType authType = AuthType.basic;
   
   public void setRealmName(String realmName)
   {
      this.realmName = realmName;
   }
   
   public String getRealmName()
   {
      return realmName;
   }
   
   public void setAuthType(AuthType authType)
   {
      this.authType = authType;
   }
   
   public AuthType getAuthType()
   {
      return authType;
   }
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
      throws IOException, ServletException
   {
      if (!(request instanceof HttpServletRequest)) 
      {
         throw new ServletException("This filter can only process HttpServletRequest requests");
      }

      HttpServletRequest httpRequest = (HttpServletRequest) request;
      HttpServletResponse httpResponse = (HttpServletResponse) response;

      switch (authType)
      {
         case basic:
            processBasicAuth(httpRequest, httpResponse, chain);
            break;
         case digest:
            processDigestAuth(httpRequest, httpResponse, chain);
            break;
      }      
   }
   
   private void processBasicAuth(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException
   {
      Context ctx = new WebSessionContext(new ServletSessionImpl(request.getSession()));
      Identity identity = (Identity) ctx.get(Identity.class);
      
      String header = request.getHeader("Authorization");
      if (header != null && header.startsWith("Basic "))
      {
         String base64Token = header.substring(6);
         String token = new String(Base64.decode(base64Token));

         String username = "";
         String password = "";
         int delim = token.indexOf(":");

         if (delim != -1) 
         {
             username = token.substring(0, delim);
             password = token.substring(delim + 1);
         }

         // Only reauthenticate if username doesn't match Identity.username and user isn't authenticated

         if (!username.equals(identity.getUsername()) || !identity.isLoggedIn()) 
         {
            identity.setUsername(username);
            identity.setPassword(password);
         }         
      }
      
      try
      {
         chain.doFilter(request, response);
         return;
      }
      catch (NotLoggedInException ex) {}
      
      if (!identity.isLoggedIn())
      {
         response.addHeader("WWW-Authenticate", "Basic realm=\"" + realmName + "\"");
         response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Not authorized");         
      }
   }

   private void processDigestAuth(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException
   {
      String header = request.getHeader("Authorization");
      if (!header.startsWith("Digest "))
      {
         throw new IllegalArgumentException("Request contains invalid authorization type");
      }      
   }
}
