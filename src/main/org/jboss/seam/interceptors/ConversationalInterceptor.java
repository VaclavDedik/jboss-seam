//$Id$
package org.jboss.seam.interceptors;

import java.lang.reflect.Method;

import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseId;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.BeginTask;
import org.jboss.seam.annotations.Conversational;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Interceptor;
import org.jboss.seam.annotations.StartTask;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Manager;

/**
 * Check that a conversational bean is not being invoked
 * outside the scope of a long-running conversation.
 * 
 * @author Gavin King
 */
@Interceptor(stateless=true,
             around={ValidationInterceptor.class, BijectionInterceptor.class, OutcomeInterceptor.class, BusinessProcessInterceptor.class})
public class ConversationalInterceptor extends AbstractInterceptor
{

   private static final Log log = LogFactory.getLog(ConversationalInterceptor.class);

   @AroundInvoke
   public Object checkConversationForConversationalBean(InvocationContext invocation) throws Exception
   {
      Method method = invocation.getMethod();

      if ( isNoConversationForConversationalBean(method) )
      {
         String outcome = methodIsConversational(method) ? 
               method.getAnnotation(Conversational.class).ifNotBegunOutcome() :
               getComponent().getBeanClass().getAnnotation(Conversational.class).ifNotBegunOutcome();
         
         if ( "".equals(outcome) )
         {
            throw new IllegalStateException( "no long-running conversation for @Conversational bean: " + getComponent().getName() );
         }
         else
         {
            //Deprecated functionality:
            if ( Lifecycle.getPhaseId()==PhaseId.INVOKE_APPLICATION )
            {
               
               if ( log.isDebugEnabled() )
               {
                  log.debug( "no long-running conversation for @Conversational bean: " + getComponent().getName() );
               }
               FacesMessages.instance().addFromResourceBundle( 
                     FacesMessage.SEVERITY_WARN, 
                     "org.jboss.seam.NoConversation", 
                     "No conversation" 
                  );
               
               if ( method.getReturnType().equals(String.class) )
               {
                  return outcome;
               }
               else if ( method.getReturnType().equals(void.class) )
               {
                  return null;
               }
            }
         }
         
      }

      return invocation.proceed();
   
   }
   
   private boolean isNoConversationForConversationalBean(Method method)
   {
      boolean classlevelViolation = componentIsConversational() && 
            ( !Manager.instance().isLongRunningConversation() || ( componentShouldBeInitiator() && !componentIsInitiator() ) ) &&
            !method.isAnnotationPresent(Begin.class) &&
            !method.isAnnotationPresent(StartTask.class) &&
            !method.isAnnotationPresent(BeginTask.class) &&
            !method.isAnnotationPresent(Destroy.class) && 
            !method.isAnnotationPresent(Create.class); //probably superfluous
      
      if (classlevelViolation) return true;
      
      boolean methodlevelViolation = methodIsConversational(method) &&
            ( !Manager.instance().isLongRunningConversation() || ( componentShouldBeInitiator(method) && !componentIsInitiator() ) );
      
      return methodlevelViolation;
      
   }

   private boolean methodIsConversational(Method method) {
      return method.isAnnotationPresent(Conversational.class);
   }

   private boolean componentShouldBeInitiator(Method method) {
      return method.getAnnotation(Conversational.class).initiator();
   }

   private boolean componentIsConversational() {
      return getComponent().getBeanClass().isAnnotationPresent(Conversational.class);
   }

   private boolean componentShouldBeInitiator() {
      return getComponent().getBeanClass().getAnnotation(Conversational.class).initiator();
   }

   private boolean componentIsInitiator()
   {
      return getComponent().getName().equals( Manager.instance().getCurrentConversationInitiator() );
   }

}
