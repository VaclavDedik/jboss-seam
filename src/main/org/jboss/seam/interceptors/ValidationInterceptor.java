//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;
import java.util.Iterator;

import javax.ejb.AroundInvoke;
import javax.ejb.InvocationContext;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.hibernate.validator.InvalidValue;
import org.jboss.logging.Logger;
import org.jboss.seam.annotations.IfInvalid;
import org.jboss.seam.annotations.Within;

/**
 * Validate the method receiver using Hibernate validator before
 * invoking the method. If a validation failure occurs, put
 * information about the failure in the request context and
 * return a different outcome, without invoking the method.
 * 
 * @author Gavin King
 */
@Within({BijectionInterceptor.class, OutcomeInterceptor.class})
public class ValidationInterceptor extends AbstractInterceptor
{

   private static final Logger log = Logger.getLogger(ValidationInterceptor.class);

   @AroundInvoke
   public Object validateTargetComponent(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();
      if ( method.isAnnotationPresent(IfInvalid.class) )
      {
         IfInvalid ifInvalid = method.getAnnotation(IfInvalid.class);
         InvalidValue[] invalidValues = component.getValidator()
               .getInvalidValues( invocation.getBean() );
         if (invalidValues.length==0)
         {
            return invocation.proceed();
         }
         else
         {
            log.info("invalid component state: " + component.getName());
            for (InvalidValue iv : invalidValues)
            {
               FacesContext facesContext = FacesContext.getCurrentInstance();
               String clientId = getClientId( facesContext.getViewRoot(), iv.getPropertyName(), facesContext);     
               FacesMessage facesMessage = new FacesMessage( FacesMessage.SEVERITY_INFO, iv.getMessage(), iv.getMessage() );
               log.info("invalid value:" + iv + ", clientId: " + clientId);
               facesContext.addMessage( clientId, facesMessage );
            }
            return ifInvalid.outcome();
         }
      }
      else
      {
         return invocation.proceed();
      }
   }
   
   private static String getClientId(UIComponent component, String id, FacesContext facesContext)
   {
      String componentId = component.getId();
      if (componentId!=null && componentId.equals(id))
      {
         return component.getClientId(facesContext);
      }
      else
      {
         Iterator iter = component.getFacetsAndChildren();
         while ( iter.hasNext() )
         {
            UIComponent child = (UIComponent) iter.next();
            String clientId = getClientId(child, id, facesContext);
            if (clientId!=null) return clientId;
         }
         return null;
      }
   }
   

}
