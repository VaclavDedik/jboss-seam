/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;

import org.jboss.seam.contexts.ContextAdaptor;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class ServletSessionImpl extends ContextAdaptor
{
   
   private HttpSession session;

   public ServletSessionImpl(HttpSession session)
   {
      this.session = session;
   }

   @Override
   public Object getAttribute(String key)
   {
      return session.getAttribute(key);
   }

   @Override
   public void removeAttribute(String key)
   {
      session.removeAttribute(key);
   }

   @Override
   public Enumeration getAttributeNames()
   {
      return session.getAttributeNames();
   }

   @Override
   public void setAttribute(String key, Object value)
   {
      session.setAttribute(key, value);
   }

   @Override
   public void invalidate()
   {
      session.invalidate();
   }

}
