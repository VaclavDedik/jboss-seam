/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Exceptions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.util.Transactions;

/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @author Gavin King
 */
public class SeamExceptionFilter extends SeamFilter
{
   
   private static final LogProvider log = Logging.getLogProvider(SeamExceptionFilter.class);

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
         log.error("uncaught exception", e);
         if (e instanceof ServletException)
         {
            log.error("exception root cause", ( (ServletException) e ).getRootCause() );
         }
         rollbackTransactionIfNecessary();
         endWebRequestAfterException( (HttpServletRequest) request, (HttpServletResponse) response, e);
      }
      finally
      {
         Lifecycle.setPhaseId(null);
      }
   }

   private void endWebRequestAfterException(HttpServletRequest request, HttpServletResponse response, Exception e) 
         throws ServletException, IOException
   {
      log.debug("ending request");
      //the FacesContext is gone - create a fake one for Redirect and HttpError to call
      MockFacesContext facesContext = createFacesContext(request, response);
      facesContext.setCurrent();
      Lifecycle.beginExceptionRecovery( getServletContext(), request ); //the faces ExternalContext is useless to us at this point
      try
      {
         Exceptions.instance().handle(e);
      }
      catch (ServletException se)
      {
         throw se;
      }
      catch (IOException ioe)
      {
         throw ioe;
      }
      catch (Exception ehe)
      {
         throw new ServletException(ehe);
      }
      finally
      {
         try 
         {
            Lifecycle.endRequest();
            facesContext.release();
            log.debug("ended request");
         }
         catch (Exception ere)
         {
            log.error("could not destroy contexts", e);
         }
      }
   }

   private MockFacesContext createFacesContext(HttpServletRequest request, HttpServletResponse response)
   {
      return new MockFacesContext( new MockExternalContext(getServletContext(), request, response), new MockApplication() );
   }

   private void rollbackTransactionIfNecessary()
   {
      try {
         if ( Transactions.isTransactionActiveOrMarkedRollback() )
         {
            log.debug("killing transaction");
            Transactions.getUserTransaction().rollback();
         }
      }
      catch (Exception te)
      {
         log.error("could not roll back transaction", te);
      }
   }

}
