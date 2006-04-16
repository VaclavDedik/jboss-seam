//$Id$
package org.jboss.seam.mock;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class MockServletContext implements ServletContext
{
   
   private Map<String, String> initParameters = new HashMap<String, String>();
   private Map<String, Object> attributes = new HashMap<String, Object>();
   
   public Map<String, String> getInitParameters()
   {
      return initParameters;
   }

   public Map<String, Object> getAttributes()
   {
      return attributes;
   }

   public ServletContext getContext(String arg0)
   {
      return this;
   }

   public int getMajorVersion()
   {
      return 2;
   }

   public int getMinorVersion()
   {
      return 4;
   }

   public String getMimeType(String arg0)
   {
      return null;
   }

   public Set getResourcePaths(String arg0)
   {
      return null;
   }

   public URL getResource(String arg0) throws MalformedURLException
   {
      return null;
   }

   public InputStream getResourceAsStream(String arg0)
   {
      return null;
   }

   public RequestDispatcher getRequestDispatcher(String arg0)
   {
      return null;
   }

   public RequestDispatcher getNamedDispatcher(String arg0)
   {
      return null;
   }

   public Servlet getServlet(String arg0) throws ServletException
   {
      return null;
   }

   public Enumeration getServlets()
   {
      return null;
   }

   public Enumeration getServletNames()
   {
      return null;
   }

   public void log(String arg0)
   {
      
   }

   public void log(Exception arg0, String arg1)
   {
      
   }

   public void log(String arg0, Throwable arg1)
   {

   }

   public String getRealPath(String arg0)
   {
      return null;
   }

   public String getServerInfo()
   {
      return null;
   }

   public String getInitParameter(String param)
   {
      return initParameters.get(param);
   }

   public Enumeration getInitParameterNames()
   {
      return new IteratorEnumeration( initParameters.keySet().iterator() );
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public void setAttribute(String att, Object val)
   {
      attributes.put(att, val);
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public String getServletContextName()
   {
      return "Mock";
   }
   
   public String getContextPath() {
      return null;
   }

}
