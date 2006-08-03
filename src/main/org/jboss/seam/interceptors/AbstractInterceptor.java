//$Id$
package org.jboss.seam.interceptors;

import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.interceptor.InvocationContext;

import org.jboss.seam.Component;

/**
 * Superclass of built-in interceptors
 * 
 * @author Gavin King
 */
class AbstractInterceptor
{
   protected transient Component component;
   private String componentName;

   public void setComponent(Component component)
   {
      this.component = component;
   }
   
   @PrePassivate
   public void initComponentName(InvocationContext invocation)
   {
      try
      {
         invocation.proceed();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("exception in @PrePassivate", e);
      }
      componentName = component.getName();
   }
   
   @PostActivate
   public void initComponent(InvocationContext invocation)
   {
      component = Component.forName(componentName);
      try
      {
         invocation.proceed();
      }
      catch (RuntimeException e)
      {
         throw e;
      }
      catch (Exception e)
      {
         throw new RuntimeException("exception in @PostActivate", e);
      }
   }

}
