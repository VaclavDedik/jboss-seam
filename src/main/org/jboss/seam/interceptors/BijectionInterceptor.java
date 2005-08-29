//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;

/**
 * Before invoking the component, inject all dependencies. After
 * invoking, outject dependencies back into their context.
 * 
 * @author Gavin King
 */
public class BijectionInterceptor extends AbstractInterceptor
{
   
   private static final Logger log = Logger.getLogger(BijectionInterceptor.class);

   @AroundInvoke
   public Object bijectTargetComponent(InvocationContext invocation) throws Exception
   {
      if ( component.getInFields().size()>0 || component.getInMethods().size()>0 ) //only needed to hush the log message
      {
         log.info("injecting dependencies of: " + component.getName());
         component.inject(invocation.getBean(), true);
      }
      
      Object result = invocation.proceed();
      
      if ( component.getOutFields().size()>0 || component.getOutMethods().size()>0 ) //only needed to hush the log message
      {
         log.info("outjecting dependencies of: " + component.getName());
         component.outject(invocation.getBean());
      }
      
      return result;
   }

}
