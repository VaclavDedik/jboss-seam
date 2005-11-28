/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class MockHttpServletRequest implements HttpServletRequest
{
   
//   private HttpSession session;
   
   private Map<String, String[]> parameters = new HashMap<String, String[]>();
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private HttpSession session;
   
   public MockHttpServletRequest(MockHttpSession session)
   {
      this.session = session;
   }
   
   public MockHttpServletRequest()
   {
   }

   public Map<String, String[]> getParameters()
   {
      return parameters;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }
   
   public String getAuthType()
   {
      //TODO
      return null;
   }

   public Cookie[] getCookies()
   {
      //TODO
      return null;
   }

   public long getDateHeader(String arg0)
   {
      //TODO
      return 0;
   }

   public String getHeader(String arg0)
   {
      //TODO
      return null;
   }

   public Enumeration getHeaders(String arg0)
   {
      //TODO
      return null;
   }

   public Enumeration getHeaderNames()
   {
      //TODO
      return null;
   }

   public int getIntHeader(String arg0)
   {
      //TODO
      return 0;
   }

   public String getMethod()
   {
      //TODO
      return null;
   }

   public String getPathInfo()
   {
      //TODO
      return null;
   }

   public String getPathTranslated()
   {
      //TODO
      return null;
   }

   public String getContextPath()
   {
      //TODO
      return null;
   }

   public String getQueryString()
   {
      //TODO
      return null;
   }

   public String getRemoteUser()
   {
      //TODO
      return null;
   }

   public boolean isUserInRole(String arg0)
   {
      //TODO
      return false;
   }

   public Principal getUserPrincipal()
   {
      //TODO
      return null;
   }

   public String getRequestedSessionId()
   {
      //TODO
      return null;
   }

   public String getRequestURI()
   {
      //TODO
      return null;
   }

   public StringBuffer getRequestURL()
   {
      //TODO
      return null;
   }

   public String getServletPath()
   {
      //TODO
      return null;
   }

   public HttpSession getSession(boolean create)
   {
      if (create && session==null)
      {
         session = new MockHttpSession();
      }
      return session;
   }

   public HttpSession getSession()
   {
      return getSession(true);
   }

   public boolean isRequestedSessionIdValid()
   {
      //TODO
      return false;
   }

   public boolean isRequestedSessionIdFromCookie()
   {
      //TODO
      return false;
   }

   public boolean isRequestedSessionIdFromURL()
   {
      //TODO
      return false;
   }

   public boolean isRequestedSessionIdFromUrl()
   {
      //TODO
      return false;
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public String getCharacterEncoding()
   {
      //TODO
      return null;
   }

   public void setCharacterEncoding(String arg0)
         throws UnsupportedEncodingException
   {
      //TODO

   }

   public int getContentLength()
   {
      //TODO
      return 0;
   }

   public String getContentType()
   {
      //TODO
      return null;
   }

   public ServletInputStream getInputStream() throws IOException
   {
      //TODO
      return null;
   }

   public String getParameter(String param)
   {
      String[] values = parameters.get(param);
      return values==null ? null : values[0];
   }

   public Enumeration getParameterNames()
   {
      return new IteratorEnumeration( parameters.keySet().iterator() );
   }

   public String[] getParameterValues(String param)
   {
      return parameters.get(param);
   }

   public Map getParameterMap()
   {
      return parameters;
   }

   public String getProtocol()
   {
      //TODO
      return null;
   }

   public String getScheme()
   {
      //TODO
      return null;
   }

   public String getServerName()
   {
      //TODO
      return null;
   }

   public int getServerPort()
   {
      //TODO
      return 0;
   }

   public BufferedReader getReader() throws IOException
   {
      //TODO
      return null;
   }

   public String getRemoteAddr()
   {
      //TODO
      return null;
   }

   public String getRemoteHost()
   {
      //TODO
      return null;
   }

   public void setAttribute(String att, Object value)
   {
      attributes.put(att, value);
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public Locale getLocale()
   {
      //TODO
      return null;
   }

   public Enumeration getLocales()
   {
      //TODO
      return null;
   }

   public boolean isSecure()
   {
      //TODO
      return false;
   }

   public RequestDispatcher getRequestDispatcher(String arg0)
   {
      //TODO
      return null;
   }

   public String getRealPath(String arg0)
   {
      //TODO
      return null;
   }

   public int getRemotePort()
   {
      //TODO
      return 0;
   }

   public String getLocalName()
   {
      //TODO
      return null;
   }

   public String getLocalAddr()
   {
      //TODO
      return null;
   }

   public int getLocalPort()
   {
      //TODO
      return 0;
   }

}
