//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.InvocationContext;

import org.jboss.logging.Logger;

public class BijectionInterceptor extends AbstractInterceptor
{
   
   private static final Logger log = Logger.getLogger(BijectionInterceptor.class);

   @Override
   public Object afterReturn(Object result, InvocationContext invocation)
   {
      outject( invocation.getBean() );
      return result;
   }

   @Override
   public Object beforeInvoke(InvocationContext invocation)
   {
      inject( invocation.getBean() );
      return null;
   }

   private void outject(final Object bean)
   {
      if ( component.getOutFields().size()>0 || component.getOutMethods().size()>0 ) //only needed to hush the log message
      {
         log.info("outjecting dependencies of: " + component.getName());
         component.outject(bean);
      }
   }

   private void inject(final Object bean)
   {
      if ( component.getInFields().size()>0 || component.getInMethods().size()>0 ) //only needed to hush the log message
      {
         log.info("injecting dependencies of: " + component.getName());
         component.inject(bean);
      }
   }

}
