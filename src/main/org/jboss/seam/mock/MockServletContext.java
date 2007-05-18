//$Id$
package org.jboss.seam.mock;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
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

   public ServletContext getContext(String name)
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

   public Set getResourcePaths(String name)
   {
      String path = getClass().getResource("/WEB-INF/web.xml").getPath();
      File rootFile = new File(path).getParentFile().getParentFile();
      Set<String> result = new HashSet<String>();
      File[] files = rootFile.listFiles();
      addPaths( result, files, rootFile.getPath() );
      return result;
   }

   private static void addPaths(Set<String> result, File[] files, String rootPath)
   {
      for (File file: files)
      {
         //apparently Tomcat does not recurse:
         /*if ( file.isDirectory() )
         {
            addPaths( result, file.listFiles(), rootPath );
         }*/
         result.add( file.getPath().substring( rootPath.length() ) );
      }
   }

   public URL getResource(String name) throws MalformedURLException
   {
      return getClass().getResource(name);
   }

   public InputStream getResourceAsStream(String name)
   {
      return getClass().getResourceAsStream(name);
   }

   public RequestDispatcher getRequestDispatcher(String url)
   {
      throw new UnsupportedOperationException();
   }

   public RequestDispatcher getNamedDispatcher(String name)
   {
      throw new UnsupportedOperationException();
   }

   public Servlet getServlet(String name) throws ServletException
   {
      throw new UnsupportedOperationException();
   }

   public Enumeration getServlets()
   {
      return null;
   }

   public Enumeration getServletNames()
   {
      return null;
   }

   public void log(String msg)
   {
      
   }

   public void log(Exception ex, String msg)
   {
      
   }

   public void log(String msg, Throwable ex)
   {

   }

   public String getRealPath(String relativePath)
   {
      return relativePath;
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
