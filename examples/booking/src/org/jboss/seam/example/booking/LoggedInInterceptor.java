//$Id$
package org.jboss.seam.example.booking;

import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;
import org.jboss.seam.annotations.Before;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.interceptors.AbstractInterceptor;
import org.jboss.seam.interceptors.BijectionInterceptor;

public class LoggedInInterceptor extends AbstractInterceptor<LoggedIn>
{
   private static final Logger log = Logger.getLogger(LoggedInInterceptor.class);

   @Before(BijectionInterceptor.class)
   public Object beforeInvoke(InvocationContext invocation)
   {
      boolean isLoggedIn = Contexts.getSessionContext().get("loggedIn")!=null;
      if (isLoggedIn) 
      {
         log.info("User is already logged in");
         return null;
      }
      else 
      {
         log.info("User is not logged in");
         return "login";
      }
   }

}
