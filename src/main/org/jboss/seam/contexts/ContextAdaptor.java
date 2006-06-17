/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.Enumeration;

import javax.faces.context.ExternalContext;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.portlet.PortletRequestImpl;
import org.jboss.seam.portlet.PortletSessionImpl;
import org.jboss.seam.servlet.ServletRequestImpl;
import org.jboss.seam.servlet.ServletSessionImpl;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public abstract class ContextAdaptor
{

   public abstract Object getAttribute(String key);

   public abstract void removeAttribute(String key);

   public abstract Enumeration getAttributeNames();

   public abstract void setAttribute(String key, Object value);

   public abstract void invalidate();
   
   public static ContextAdaptor getSession(ExternalContext externalContext, boolean create)
   {
      Object session = externalContext.getSession(true);
      if (session instanceof HttpSession)
      {
         return new ServletSessionImpl((HttpSession) session);
      }
      else if (session instanceof PortletSession)
      {
         return new PortletSessionImpl((PortletSession) session);
      }
      else 
      {
         throw new RuntimeException("Unknown type of session");
      }
   }

   public static ContextAdaptor getSession(HttpSession session)
   {
      return new ServletSessionImpl(session);
   }

   public static ContextAdaptor getRequest(ExternalContext externalContext)
   {
      Object request = externalContext.getRequest();
      if (request instanceof HttpServletRequest)
      {
         return new ServletRequestImpl((ServletRequest) request);
      }
      else if (request instanceof PortletRequest)
      {
         return new PortletRequestImpl((PortletRequest) request);
      }
      else 
      {
         throw new RuntimeException("Unknown type of request");
      }
   }

   public static ContextAdaptor getRequest(ServletRequest request)
   {
      return new ServletRequestImpl(request);
   }
}


