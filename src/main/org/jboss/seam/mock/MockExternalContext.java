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
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class MockExternalContext extends ExternalContext
{
   private ServletContext context;
   private HttpServletRequest request;
   private HttpServletResponse response;
   
   
   public MockExternalContext()
   {
      this.context = new MockServletContext();
      this.request = new MockHttpServletRequest( new MockHttpSession(context) );
   }

   public MockExternalContext(ServletContext context)
   {
      this.context = context;
      this.request = new MockHttpServletRequest( new MockHttpSession(context) );
   }

   public MockExternalContext(ServletContext context, HttpSession session)
   {
      this.context = context;
      this.request = new MockHttpServletRequest(session);
   }

   public MockExternalContext(ServletContext context, HttpServletRequest request)
   {
      this.context = context;
      this.request = request;
   }
   
   public MockExternalContext(ServletContext context, HttpServletRequest request, HttpServletResponse response)
   {
      this.context = context;
      this.request = request;
      this.response = response;
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
      Map result = new HashMap();
      Enumeration e = context.getAttributeNames();
      while ( e.hasMoreElements() )
      {
         String name = (String) e.nextElement();
         result.put( name, context.getAttribute(name) );
      }
      return result;
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
   public String getInitParameter(String name)
   {
      return context.getInitParameter(name);
   }

   @Override
   public Map getInitParameterMap()
   {
      Map result = new HashMap();
      Enumeration e = context.getInitParameterNames();
      while ( e.hasMoreElements() )
      {
         String name = (String) e.nextElement();
         result.put( name, context.getInitParameter(name) );
      }
      return result;
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
      Map result = new HashMap();
      Enumeration e = request.getAttributeNames();
      while ( e.hasMoreElements() )
      {
         String name = (String) e.nextElement();
         result.put( name, request.getAttribute(name) );
      }
      return result;
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
      return response;
   }

   @Override
   public Object getSession(boolean create)
   {
      return request.getSession();
   }

   @Override
   public Map getSessionMap()
   {
      Map result = new HashMap();
      HttpSession session = request.getSession(true);
      Enumeration e = session.getAttributeNames();
      while ( e.hasMoreElements() )
      {
         String name = (String) e.nextElement();
         result.put( name, session.getAttribute(name) );
      }
      return result;
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
   public void log(String message, Throwable t)
   {
      
   }

   @Override
   public void log(String t)
   {
   }

   @Override
   public void redirect(String url) throws IOException
   {
      response.sendRedirect(url);
      FacesContext.getCurrentInstance().responseComplete(); 
   }

}
