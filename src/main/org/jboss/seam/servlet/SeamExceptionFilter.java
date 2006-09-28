/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.util.Transactions;

/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @author Gavin King
 */
public class SeamExceptionFilter implements Filter
{
   
   private static final Log log = LogFactory.getLog(SeamExceptionFilter.class);
   private ServletContext context;

   public void init(FilterConfig cfg) throws ServletException {
      context = cfg.getServletContext();
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
         throws IOException, ServletException
   {
      try
      {
         chain.doFilter(request, response);
         
         //There is a bug in JBoss AS where JBoss does not clean up
         //any orphaned tx at the end of the request. It is possible
         //that a Seam-managed tx could be left orphaned if, eg.
         //facelets handles an exceptions and displays the debug page.
         rollbackTransactionIfNecessary(); 
      }
      catch (Exception e)
      {
         rollbackTransactionIfNecessary();
         endWebRequestAfterException(request);
         if ( !isExceptionHandled(request) )
         {
            log.error("uncaught exception handled by Seam", e);
            throw new ServletException(e);
         }
      }
      finally
      {
         Lifecycle.setPhaseId(null);
         log.debug("ended request");
      }
   }

   private boolean isExceptionHandled(ServletRequest request)
   {
      return request.getAttribute("org.jboss.seam.exceptionHandled")!=null;
   }

   private void endWebRequestAfterException(ServletRequest request)
   {
      Lifecycle.setException(true);
      try 
      {
         Lifecycle.beginExceptionRecovery(context, request); //the faces ExternalContext is useless to us at this point
         Lifecycle.endRequest();
      }
      catch (Exception ee)
      {
         log.error("could not destroy contexts", ee);
      }
      finally
      {
         Lifecycle.setException(false);
      }
   }

   private void rollbackTransactionIfNecessary()
   {
      try {
         if ( Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.info("killing transaction");
            Transactions.getUserTransaction().rollback();
         }
      }
      catch (Exception te)
      {
         log.error("could not roll back transaction", te);
      }
   }

   public void destroy() {}

}
