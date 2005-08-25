//$Id$
package org.jboss.seam.mock;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class MockHttpSession implements HttpSession
{
   
   private Map<String, Object> attributes = new HashMap<String, Object>();
   private ServletContext servletContext;
   private boolean isInvalid;
   
   public MockHttpSession(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }
   
   public boolean isInvalid()
   {
      return isInvalid;
   }

   public long getCreationTime()
   {
      //TODO
      return 0;
   }

   public String getId()
   {
      //TODO
      return null;
   }

   public long getLastAccessedTime()
   {
      //TODO
      return 0;
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

   public void setMaxInactiveInterval(int arg0)
   {
      //TODO

   }

   public int getMaxInactiveInterval()
   {
      //TODO
      return 0;
   }
   
   @SuppressWarnings("deprecation")
   public HttpSessionContext getSessionContext()
   {
      //TODO
      return null;
   }

   public Object getAttribute(String att)
   {
      return attributes.get(att);
   }

   public Object getValue(String att)
   {
      return getAttribute(att);
   }

   public Enumeration getAttributeNames()
   {
      return new IteratorEnumeration( attributes.keySet().iterator() );
   }

   public String[] getValueNames()
   {
      return attributes.keySet().toArray( new String[0] );
   }

   public void setAttribute(String att, Object value)
   {
      attributes.put(att, value);
   }

   public void putValue(String att, Object value)
   {
      setAttribute(att, value);
   }

   public void removeAttribute(String att)
   {
      attributes.remove(att);
   }

   public void removeValue(String att)
   {
      removeAttribute(att);
   }

   public void invalidate()
   {
      attributes.clear();
      isInvalid = true;
   }

   public boolean isNew()
   {
      return false;
   }

}
