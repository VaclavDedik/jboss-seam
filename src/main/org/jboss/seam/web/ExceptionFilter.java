/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.web;

import static org.jboss.seam.InterceptionType.NEVER;
import static org.jboss.seam.ScopeType.APPLICATION;
import static org.jboss.seam.annotations.Install.BUILT_IN;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.seam.annotations.Filter;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Intercept;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Exceptions;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.transaction.Transaction;
import org.jboss.seam.util.EJB;

/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @author Gavin King
 */
@Startup
@Scope(APPLICATION)
@Name("org.jboss.seam.web.exceptionFilter")
@Install(precedence = BUILT_IN)
@Intercept(NEVER)
@Filter(within="org.jboss.seam.web.ajax4jsfFilter")
public class ExceptionFilter extends AbstractFilter
{
   
   private static final LogProvider log = Logging.getLogProvider(ExceptionFilter.class);
   
   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
         throws IOException, ServletException
   {
      try
      {
         chain.doFilter(request, response);
      }
      catch (Exception e)
      {
         log.error("uncaught exception", e);
         log.error( "exception root cause", EJB.getCause(e) );
         endWebRequestAfterException( (HttpServletRequest) request, (HttpServletResponse) response, e);
      }
      finally
      {
         Lifecycle.setPhaseId(null);
      }
   }
   
   protected void endWebRequestAfterException(HttpServletRequest request, HttpServletResponse response, Exception e) 
         throws ServletException, IOException
   {
      log.debug("ending request");
      //the FacesContext is gone - create a fake one for Redirect and HttpError to call
      MockFacesContext facesContext = createFacesContext(request, response);
      facesContext.setCurrent();
      Lifecycle.beginExceptionRecovery( facesContext.getExternalContext() );
      try
      {
         rollbackTransactionIfNecessary();
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
            Lifecycle.endRequest( facesContext.getExternalContext() );
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
   
   protected void rollbackTransactionIfNecessary()
   {
      try 
      {
         if ( Transaction.instance().isActiveOrMarkedRollback() )
         {
            log.debug("killing transaction");
            Transaction.instance().rollback();
         }
      }
      catch (Exception te)
      {
         log.error("could not roll back transaction", te);
      }
   }
}
