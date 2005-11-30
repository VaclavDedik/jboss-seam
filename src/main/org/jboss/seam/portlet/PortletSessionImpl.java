/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.portlet;

import java.util.Enumeration;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletSession;

import org.jboss.seam.Session;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public class PortletSessionImpl extends Session
{

   private PortletSession session;
   private ExternalContext externalContext;
   
   public PortletSessionImpl(ExternalContext externalContext, PortletSession session)
   {
      this.session = session;
      this.externalContext = externalContext;
   }
   
   public Object getAttribute(String key)
   {
      return session.getAttribute(key);
   }

   public void removeAttribute(String key)
   {
      session.removeAttribute(key);
   }

   public Enumeration getAttributeNames()
   {
      return session.getAttributeNames();
   }

   public void setAttribute(String key, Object value)
   {
      session.setAttribute(key, value);
   }

   public void invalidate()
   {
      session.invalidate();
   }

   @Override
   public ExternalContext getExternalContext()
   {
      return externalContext;
   }

}
