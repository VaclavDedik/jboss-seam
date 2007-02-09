//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.persistence.Entity;
import javax.persistence.EntityManager;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.hibernate.Session;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.AroundInvoke;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.intercept.InvocationContext;

/**
 * Validate the method receiver using Hibernate validator before
 * invoking the method. If a validation failure occurs, put
 * information about the failure in the request context and
 * return a different outcome, without invoking the method.
 * 
 * @deprecated
 * @author Gavin King
 */
@Interceptor(stateless=true,
             within={BijectionInterceptor.class, OutcomeInterceptor.class})
@SuppressWarnings("deprecation")
public class ValidationInterceptor extends AbstractInterceptor
{
   private static final long serialVersionUID = 4724500409653141512L;
  
   private static final LogProvider log = Logging.getLogProvider(ValidationInterceptor.class);
   
   @AroundInvoke
   public Object aroundInvoke(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();
      if ( method.isAnnotationPresent(IfInvalid.class) )
      {
         IfInvalid ifInvalid = method.getAnnotation(IfInvalid.class);
         InvalidValue[] invalidValues = getComponent().getValidator()
               .getInvalidValues( invocation.getTarget() );
         if (invalidValues.length==0)
         {
            return invocation.proceed();
         }
         else
         {
            log.debug("invalid component state: " + getComponent().getName());
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
