package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Identity;
import org.jboss.seam.security.NotLoggedInException;
import org.jboss.seam.security.digest.DigestRequest;
import org.jboss.seam.security.digest.DigestUtils;
import org.jboss.seam.security.digest.DigestValidationException;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.jboss.seam.util.Base64;

/**
 * Seam Servlet Filter supporting HTTP Basic and Digest authentication. Some code
 * adapted from Acegi.
 *  
 * @author Shane Bryzak
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.web.httpAuthFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@Filter(within = "org.jboss.seam.web.exceptionFilter")
public class HttpAuthFilter extends AbstractFilter
{
   private static final String DEFAULT_REALM = "seamApp";
   
   @Logger Log log;
   
   public enum AuthType {basic, digest}
   
   private String realmName = DEFAULT_REALM;
   
   private String key;
   private int nonceValiditySeconds = 300;
   
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
   
   public String getKey()
   {
      return key;
   }
   
   public void setKey(String key)
   {
      this.key = key;
   }
   
   public int getNonceValiditySeconds()
   {
      return nonceValiditySeconds;
   }
   
   public void setNonceValiditySeconds(int value)
   {
      this.nonceValiditySeconds = value;
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
      Context ctx = new WebSessionContext(new ServletSessionImpl(request.getSession()));
      Identity identity = (Identity) ctx.get(Identity.class);
      
      boolean failed = false;    
      boolean nonceExpired = false;
      
      String header = request.getHeader("Authorization");      
      if (header != null && header.startsWith("Digest "))
      {
         String section212response = header.substring(7);

         String[] headerEntries = section212response.split(",");
         Map<String,String> headerMap = new HashMap<String,String>();
         for (String entry : headerEntries)
         {
            String[] vals = entry.split("=");
            headerMap.put(vals[0].trim(), vals[1].replace("\"", "").trim());
         }
         
         identity.setUsername(headerMap.get("username"));

         DigestRequest digestRequest = new DigestRequest();
         digestRequest.setSystemRealm(realmName);
         digestRequest.setRealm(headerMap.get("realm"));         
         digestRequest.setKey(key);
         digestRequest.setNonce(headerMap.get("nonce"));
         digestRequest.setUri(headerMap.get("uri"));
         digestRequest.setClientDigest(headerMap.get("response"));
         digestRequest.setQop(headerMap.get("qop"));
         digestRequest.setNonceCount(headerMap.get("nc"));
         digestRequest.setClientNonce(headerMap.get("cnonce"));
                  
         try
         {
            digestRequest.validate();
            ctx.set(DigestRequest.DIGEST_REQUEST, digestRequest);
         }
         catch (DigestValidationException ex)
         {
            log.error(String.format("Digest validation failed, header [%s]: %s",
                     section212response, ex.getMessage()));
            failed = true;
            
            if (ex.isNonceExpired()) nonceExpired = true;
         }            
      }
      
      if (!failed)
      {
         try
         {
            chain.doFilter(request, response);
            return;
         }
         catch (NotLoggedInException ex) {}
      }
      
      if (failed || !identity.isLoggedIn())
      {
         long expiryTime = System.currentTimeMillis() + (nonceValiditySeconds * 1000);
         
         String signatureValue = DigestUtils.md5Hex(expiryTime + ":" + key);
         String nonceValue = expiryTime + ":" + signatureValue;
         String nonceValueBase64 = Base64.encodeBytes(nonceValue.getBytes());

         // qop is quality of protection, as defined by RFC 2617.
         // we do not use opaque due to IE violation of RFC 2617 in not
         // representing opaque on subsequent requests in same session.
         String authenticateHeader = "Digest realm=\"" + realmName + "\", " + "qop=\"auth\", nonce=\""
             + nonceValueBase64 + "\"";

         if (nonceExpired) authenticateHeader = authenticateHeader + ", stale=\"true\"";

         response.addHeader("WWW-Authenticate", authenticateHeader);
         response.sendError(HttpServletResponse.SC_UNAUTHORIZED);      
      }             
   }
}
