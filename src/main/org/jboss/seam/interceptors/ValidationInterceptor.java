//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.ejb.InvocationContext;

import org.hibernate.validator.InvalidValue;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.contexts.Contexts;

/**
 * Validate the method receiver using Hibernate validator before
 * invoking the method. If a validation failure occurs, put
 * information about the failure in the request context and
 * return a different outcome, without invoking the method.
 * 
 * @author Gavin King
 */
@Within(BijectionInterceptor.class)
public class ValidationInterceptor extends AbstractInterceptor
{

   private static final Logger log = Logger.getLogger(ValidationInterceptor.class);

   @Override
   public Object beforeInvoke(InvocationContext invocation)
   {
      return validateIfNecessary( invocation.getBean(), invocation.getMethod() );
   }
   
   private String validateIfNecessary(Object bean, Method method)
   {
      if ( method.isAnnotationPresent(IfInvalid.class) )
      {
         return validate( bean, method.getAnnotation(IfInvalid.class) );
      }
      else
      {
         return null;
      }
   }

   private String validate(Object bean, IfInvalid ifInvalid)
   {
      InvalidValue[] invalidValues = component.getValidator().getInvalidValues(bean);
      if (invalidValues.length==0)
      {
         return null;
      }
      else
      {
         log.info("invalid component state: " + component.getName());
         Contexts.getEventContext().set(
               ifInvalid.invalidValuesName(), 
               invalidValues
            );
         return ifInvalid.outcome();
      }
   }
   

}
