//$Id$
package org.jboss.seam.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

public class MockExternalContext extends ExternalContext
{
   
   private HttpServletRequest request;
   
   MockExternalContext(HttpServletRequest request)
   {
      this.request = request;
   }

   @Override
   public void dispatch(String arg0) throws IOException
   {
      //TODO
      
   }

   @Override
   public String encodeActionURL(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public String encodeNamespace(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public String encodeResourceURL(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public Map getApplicationMap()
   {
      //TODO
      return null;
   }

   @Override
   public String getAuthType()
   {
      //TODO
      return null;
   }

   @Override
   public Object getContext()
   {
      //TODO
      return null;
   }

   @Override
   public String getInitParameter(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public Map getInitParameterMap()
   {
      //TODO
      return null;
   }

   @Override
   public String getRemoteUser()
   {
      //TODO
      return null;
   }

   @Override
   public Object getRequest()
   {
      //TODO
      return request;
   }

   @Override
   public String getRequestContextPath()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestCookieMap()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestHeaderMap()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestHeaderValuesMap()
   {
      //TODO
      return null;
   }

   @Override
   public Locale getRequestLocale()
   {
      //TODO
      return null;
   }

   @Override
   public Iterator getRequestLocales()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestMap()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestParameterMap()
   {
      return request.getParameterMap();
   }

   @Override
   public Iterator getRequestParameterNames()
   {
      //TODO
      return null;
   }

   @Override
   public Map getRequestParameterValuesMap()
   {
      //TODO
      return null;
   }

   @Override
   public String getRequestPathInfo()
   {
      //TODO
      return null;
   }

   @Override
   public String getRequestServletPath()
   {
      //TODO
      return null;
   }

   @Override
   public URL getResource(String arg0) throws MalformedURLException
   {
      //TODO
      return null;
   }

   @Override
   public InputStream getResourceAsStream(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public Set getResourcePaths(String arg0)
   {
      //TODO
      return null;
   }

   @Override
   public Object getResponse()
   {
      //TODO
      return null;
   }

   @Override
   public Object getSession(boolean create)
   {
      //TODO
      return request.getSession();
   }

   @Override
   public Map getSessionMap()
   {
      //TODO
      return null;
   }

   @Override
   public Principal getUserPrincipal()
   {
      //TODO
      return null;
   }

   @Override
   public boolean isUserInRole(String arg0)
   {
      //TODO
      return false;
   }

   @Override
   public void log(String arg0, Throwable arg1)
   {
      
   }

   @Override
   public void log(String arg0)
   {
   }

   @Override
   public void redirect(String arg0) throws IOException
   {
      //TODO
      
   }

}
