/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class MockExternalContext extends ExternalContext
{
   private MockServletContext context;
   private HttpServletRequest request;
   
   
   public MockExternalContext()
   {
      this.context = new MockServletContext();
      this.request = new MockHttpServletRequest(this);
   }

   public MockExternalContext(MockServletContext context)
   {
      this.context = context;
      this.request = new MockHttpServletRequest(this);
   }

   public MockExternalContext(MockServletContext context, HttpServletRequest request)
   {
      this.context = context;
      this.request = request;
   }
   
   @Override
   public void dispatch(String url) throws IOException
   {
      
   }

   @Override
   public String encodeActionURL(String url)
   {
      return url;
   }

   @Override
   public String encodeNamespace(String ns)
   {
      return ns;
   }

   @Override
   public String encodeResourceURL(String url)
   {
      return url;
   }

   @Override
   public Map getApplicationMap()
   {
      return context.getAttributes();
   }

   @Override
   public String getAuthType()
   {
      return request.getAuthType();
   }

   @Override
   public Object getContext()
   {
      return context;
   }

   @Override
   public String getInitParameter(String arg0)
   {
      return context.getInitParameter(arg0);
   }

   @Override
   public Map getInitParameterMap()
   {
      return context.getInitParameters();
   }

   @Override
   public String getRemoteUser()
   {
      return request.getRemoteUser();
   }

   @Override
   public Object getRequest()
   {
      return request;
   }

   @Override
   public String getRequestContextPath()
   {
      return request.getContextPath();
   }

   @Override
   public Map getRequestCookieMap()
   {
      return null;
   }

   @Override
   public Map getRequestHeaderMap()
   {
      Map result = new HashMap();
      Enumeration<String> names = request.getHeaderNames();
      while ( names.hasMoreElements() )
      {
         String name = names.nextElement();
         result.put( name, request.getHeader(name) );
      }
      return result;
   }

   @Override
   public Map getRequestHeaderValuesMap()
   {
      Map<String, Enumeration> result = new HashMap<String, Enumeration>();
      Enumeration<String> en = request.getHeaderNames();
      while ( en.hasMoreElements() )
      {
         result.put( en.nextElement(), request.getHeaders( en.nextElement() ) );
      }
      return result;
   }

   @Override
   public Locale getRequestLocale()
   {
      return Locale.ENGLISH;
   }

   @Override
   public Iterator getRequestLocales()
   {
      return Collections.singleton(Locale.ENGLISH).iterator();
   }

   @Override
   public Map getRequestMap()
   {
      return ( (MockHttpServletRequest) request ).getAttributes();
   }

   @Override
   public Map getRequestParameterMap()
   {
      Map map = new HashMap();
      Enumeration<String> names = request.getParameterNames();
      while ( names.hasMoreElements() )
      {
         String name = names.nextElement();
         map.put( name, request.getParameter(name) );
      }
      return map;
   }

   @Override
   public Iterator getRequestParameterNames()
   {
      return request.getParameterMap().keySet().iterator();
   }

   @Override
   public Map getRequestParameterValuesMap()
   {
      return request.getParameterMap();
   }

   @Override
   public String getRequestPathInfo()
   {
      return request.getPathInfo();
   }

   @Override
   public String getRequestServletPath()
   {
      return request.getServletPath();
   }

   @Override
   public URL getResource(String name) throws MalformedURLException
   {
      return context.getResource(name);
   }

   @Override
   public InputStream getResourceAsStream(String name)
   {
      return context.getResourceAsStream(name);
   }

   @Override
   public Set getResourcePaths(String name)
   {
      return context.getResourcePaths(name);
   }

   @Override
   public Object getResponse()
   {
      return null;
   }

   @Override
   public Object getSession(boolean create)
   {
      return request.getSession();
   }

   @Override
   public Map getSessionMap()
   {
      return ( (MockHttpSession) request.getSession() ).getAttributes();
   }

   @Override
   public Principal getUserPrincipal()
   {
      return request.getUserPrincipal();
   }

   @Override
   public boolean isUserInRole(String role)
   {
      return request.isUserInRole(role);
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
   public void redirect(String url) throws IOException
   {
      
   }

}
