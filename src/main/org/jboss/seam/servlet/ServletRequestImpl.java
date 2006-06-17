/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import java.util.Enumeration;

import javax.servlet.ServletRequest;

import org.jboss.seam.contexts.ContextAdaptor;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class ServletRequestImpl extends ContextAdaptor
{
   
   private ServletRequest request;

   public ServletRequestImpl(ServletRequest request)
   {
      this.request = request;
   }

   public Object getAttribute(String key)
   {
      return request.getAttribute(key);
   }

   public void removeAttribute(String key)
   {
      request.removeAttribute(key);
   }

   public Enumeration getAttributeNames()
   {
      return request.getAttributeNames();
   }

   public void setAttribute(String key, Object value)
   {
      request.setAttribute(key, value);
   }

   public void invalidate()
   {
      throw new UnsupportedOperationException();
   }

}
