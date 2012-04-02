//$Id: MockServletContext.java 9513 2008-11-06 03:09:55Z shane.bryzak@jboss.com $
package org.jboss.seam.mock;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.util.IteratorEnumeration;
import org.jboss.seam.util.XML;

public class MockServletContext implements ServletContext
{

   private transient LogProvider log = Logging.getLogProvider(MockServletContext.class);
   
   private Map<String, String> initParameters = new HashMap<String, String>();
   private Map<String, Object> attributes = new HashMap<String, Object>();
   
   private File webappRoot;
   private File webInfRoot;
   private File webInfClassesRoot;
   
   public MockServletContext()
   {
      try
      {
         URL webxml = getClass().getResource("/WEB-INF/web.xml");
         if (webxml != null)
         {
            webInfRoot = new File(webxml.toURI()).getParentFile();
            if (webInfRoot != null)
            {
               webInfClassesRoot = new File(webInfRoot.getParentFile().getPath() + "/classes");
               webappRoot = webInfRoot.getParentFile();
            }
            // call processing of context parameters
            processContextParameters(webxml);
         }
         else
         {
            webappRoot = new File(getClass().getResource("/.").toURI());
         }
      }
      catch (URISyntaxException e)
      {
         log.warn("Unable to find web.xml", e);
      }
   }
   
   private void processContextParameters(URL webXML)
   {
      try
      {
         Element root = XML.getRootElementSafely(webXML.openStream());         
         for (Element element : (List<Element>) root.elements("context-param"))
         {
            getInitParameters().put(element.elementText("param-name"), element.elementText("param-value"));
         }
      }
      catch (IOException e) 
      {
         throw new RuntimeException("Error parsing web.xml", e);
      }
      catch (DocumentException e)
      {
         throw new RuntimeException("Error parsing web.xml", e);
      }
      

   }
   
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
      Enumeration<URL> enumeration = null;
      try
      {
         enumeration = getClass().getClassLoader().getResources("WEB-INF");
      }
      catch (IOException e)
      {
         throw new RuntimeException("Error finding webroot.", e);
      }
      Set<String> result = new HashSet<String>();
      while (enumeration.hasMoreElements())
      {
         URL url = enumeration.nextElement();
         File rootFile = new File(url.getPath()).getParentFile();
         File newFile = new File(rootFile.getPath() + name);
         File[] files = newFile.listFiles();
         if (files != null)
         {
            addPaths(result, files, rootFile.getPath());
         }
      }
      return result;
   }
   
   private static void addPaths(Set<String> result, File[] files, String rootPath)
   {
      for (File file : files)
      {
         String filePath = file.getPath().substring(rootPath.length()).replace('\\', '/');
         if (file.isDirectory())
         {
            result.add(filePath + "/");
         }
         else
         {
            result.add(filePath);
         }
      }
   }

   /**
    * Get the URL for a particular resource that is relative to the web app root
    * directory.
    * 
    * @param name The name of the resource to get
    * @return The resource, or null if resource not found
    * @throws MalformedURLException If the URL is invalid
    */
   public URL getResource(String name) throws MalformedURLException
   {
      File file = getFile(name, webappRoot);
      
      if (file == null)
      {
         file = getFile(name, webInfRoot);
      }
      
      if (file == null)
      {
         file = getFile(name, webInfClassesRoot);
      }
      
      if (file != null)
      {
         return file.toURI().toURL();
      }
      else
      {
         return null;
      }
   }

   private static File getFile(String name, File root)
   {
      if (root == null)
      {
         return null;
      }
      
      if (name.startsWith("/"))
      {
         name = name.substring(1);
      }
      
      File f = new File(root, name);
      if (!f.exists())
      {
         return null;
      }
      else
      {
         return f;
      }
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
      if (webappRoot != null)
      {
         return webappRoot.getAbsolutePath() + relativePath;
      }
      else
      {
         return relativePath;
      }
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
      return new IteratorEnumeration(initParameters.keySet().iterator());
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration(attributes.keySet().iterator());
   }

   public void setAttribute(String att, Object value)
   {
      if (value == null)
      {
         attributes.remove(value);
      }
      else
      {
         attributes.put(att, value);
      }
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public String getServletContextName()
   {
      return "Mock";
   }

   public String getContextPath()
   {
      return null;
   }

   @Override
   public int getEffectiveMajorVersion()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public int getEffectiveMinorVersion()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public boolean setInitParameter(String name, String value)
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public Dynamic addServlet(String servletName, String className)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Dynamic addServlet(String servletName, Servlet servlet)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Dynamic addServlet(String servletName, Class<? extends Servlet> servletClass)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <T extends Servlet> T createServlet(Class<T> clazz) throws ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ServletRegistration getServletRegistration(String servletName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, ? extends ServletRegistration> getServletRegistrations()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, String className)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Filter filter)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public javax.servlet.FilterRegistration.Dynamic addFilter(String filterName, Class<? extends Filter> filterClass)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public <T extends Filter> T createFilter(Class<T> clazz) throws ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public FilterRegistration getFilterRegistration(String filterName)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Map<String, ? extends FilterRegistration> getFilterRegistrations()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public SessionCookieConfig getSessionCookieConfig()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setSessionTrackingModes(Set<SessionTrackingMode> sessionTrackingModes)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public Set<SessionTrackingMode> getDefaultSessionTrackingModes()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public Set<SessionTrackingMode> getEffectiveSessionTrackingModes()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void addListener(String className)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public <T extends EventListener> void addListener(T t)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void addListener(Class<? extends EventListener> listenerClass)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public <T extends EventListener> T createListener(Class<T> clazz) throws ServletException
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public JspConfigDescriptor getJspConfigDescriptor()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ClassLoader getClassLoader()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void declareRoles(String... roleNames)
   {
      // TODO Auto-generated method stub
      
   }
   

}
