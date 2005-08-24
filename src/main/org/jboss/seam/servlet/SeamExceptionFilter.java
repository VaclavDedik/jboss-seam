//$Id$
package org.jboss.seam.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.logging.Logger;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.util.Transactions;

/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @author Gavin King
 */
public class SeamExceptionFilter implements Filter
{
   
   private static Logger log = Logger.getLogger(SeamExceptionFilter.class);

   public void init(FilterConfig cfg) throws ServletException {}

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
         throws IOException, ServletException
   {
      try
      {
         chain.doFilter(request, response);
      }
      catch (Exception e)
      {
         log.error("uncaught exception handled by Seam: "+ e.getMessage());
         rollbackAfterException();
         endWebRequestAfterException(request);
         throw new ServletException(e);
      }
   }

   private void endWebRequestAfterException(ServletRequest request)
   {
      try 
      {
         SeamPhaseListener.endWebRequest( (HttpServletRequest) request);
      }
      catch (Exception ee)
      {
         log.error("could not destroy contexts", ee);
      }
   }

   private void rollbackAfterException()
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
