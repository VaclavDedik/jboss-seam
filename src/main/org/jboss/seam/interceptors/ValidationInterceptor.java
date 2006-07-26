//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.core.FacesMessages;

/**
 * Validate the method receiver using Hibernate validator before
 * invoking the method. If a validation failure occurs, put
 * information about the failure in the request context and
 * return a different outcome, without invoking the method.
 * 
 * @author Gavin King
 */
@Interceptor(within={BijectionInterceptor.class, OutcomeInterceptor.class})
public class ValidationInterceptor extends AbstractInterceptor
{

   private static final Log log = LogFactory.getLog(ValidationInterceptor.class);

   @AroundInvoke
   public Object validateTargetComponent(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();
      if ( method.isAnnotationPresent(IfInvalid.class) )
      {
         IfInvalid ifInvalid = method.getAnnotation(IfInvalid.class);
         InvalidValue[] invalidValues = component.getValidator()
               .getInvalidValues( invocation.getTarget() );
         if (invalidValues.length==0)
         {
            return invocation.proceed();
         }
         else
         {
            log.debug("invalid component state: " + component.getName());
            for (InvalidValue iv : invalidValues)
            {
               log.debug("invalid value: " + iv);
               if ( ifInvalid.refreshEntities() && iv.getBeanClass().isAnnotationPresent(Entity.class) )
               {
                  refreshInvalidEntity( ifInvalid, iv.getBean() );
               }
               FacesMessages.instance().add(iv);
            }
            return ifInvalid.outcome();
         }
      }
      else
      {
         return invocation.proceed();
      }
   }

   private void refreshInvalidEntity(IfInvalid ifInvalid, Object entity) {
      Object persistenceContext = Component.getInstance( ifInvalid.persistenceContext(), false );
      if (persistenceContext==null) 
      {
         throw new IllegalStateException(
               "Seam-managed persistence context not found: " + 
               ifInvalid.persistenceContext()
            );
      }
      else
      {
         if (persistenceContext instanceof EntityManager)
         {
            EntityManager em = (EntityManager) persistenceContext;
            if ( em.contains(entity) )
            {
               em.refresh(entity);
            }
         }
         else
         {
            Session session = (Session) persistenceContext;
            if ( session.contains(entity) )
            {
               session.refresh(entity);
            }
         }
      }
   }
   
}
