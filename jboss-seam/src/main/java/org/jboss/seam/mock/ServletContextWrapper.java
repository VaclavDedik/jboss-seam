/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */

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

import org.jboss.seam.util.IteratorEnumeration;

/**
 * Wraps a ServletContext with own attributes.
 *
 * @author Marek Schmidt
 */
public class ServletContextWrapper implements ServletContext {
   
   private ServletContext delegate; 
   
   private Map<String, Object> attributes = new HashMap<String, Object>();
   
   public ServletContextWrapper(ServletContext delegate) {
      this.delegate = delegate;
   }

   public Object getAttribute(String arg0) {
      return attributes.get(arg0);
   }

   public Enumeration getAttributeNames() {
      return new IteratorEnumeration(attributes.keySet().iterator());
   }

   public ServletContext getContext(String arg0) {
      return delegate.getContext(arg0);
   }

   public String getContextPath() {
      return delegate.getContextPath();
   }

   public String getInitParameter(String arg0) {
      return delegate.getInitParameter(arg0);
   }

   public Enumeration getInitParameterNames() {
      return delegate.getInitParameterNames();
   }

   public int getMajorVersion() {
      return delegate.getMajorVersion();
   }

   public String getMimeType(String arg0) {
      return delegate.getMimeType(arg0);
   }

   public int getMinorVersion() {
      return delegate.getMinorVersion();
   }

   public RequestDispatcher getNamedDispatcher(String arg0) {
      return delegate.getNamedDispatcher(arg0);
   }

   public String getRealPath(String arg0) {
      return delegate.getRealPath(arg0);
   }

   public RequestDispatcher getRequestDispatcher(String arg0) {
      return delegate.getRequestDispatcher(arg0);
   }

   public URL getResource(String arg0) throws MalformedURLException {
      return delegate.getResource(arg0);
   }

   public InputStream getResourceAsStream(String arg0) {
      InputStream ret = delegate.getResourceAsStream(arg0);
      return ret;
   }

   public Set getResourcePaths(String arg0) {
      return delegate.getResourcePaths(arg0);
   }

   public String getServerInfo() {
      return delegate.getServerInfo();
   }

   public Servlet getServlet(String arg0) throws ServletException {
      return delegate.getServlet(arg0);
   }

   public String getServletContextName() {
      return "Wrap";
   }

   public Enumeration getServletNames() {
      return delegate.getServletNames();
   }

   public Enumeration getServlets() {
      return delegate.getServlets();
   }

   public void log(String arg0) {
      delegate.log(arg0);
   }

   public void log(Exception arg0, String arg1) {
      delegate.log(arg0, arg1);
   }

   public void log(String arg0, Throwable arg1) {
      delegate.log(arg0, arg1);
   }

   public void removeAttribute(String arg0) {
      attributes.remove(arg0);
   }

   public void setAttribute(String key, Object value) {
      if (value == null)
      {
         attributes.remove(key);
      }
      else
      {
         attributes.put(key, value);
      }
   }
}
