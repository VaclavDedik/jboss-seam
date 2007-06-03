/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.PROCESS_VALIDATIONS;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.portlet.ActionResponse;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Manager;
import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;

/**
 * Manages the Seam contexts associated with a JSF portlet
 * request.
 * 
 * Manages the thread/context associations throughout the
 * lifecycle of the JSF request.
 *
 * @author Gavin King
 */
public class SeamPortletPhaseListener extends AbstractSeamPhaseListener
{
   private static final long serialVersionUID = 262187729483387144L;
   
   private static final LogProvider log = Logging.getLogProvider( SeamPortletPhaseListener.class );

   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW || event.getPhaseId() == RENDER_RESPONSE )
      {
         beforeRestoreView(facesContext);
      }
      
      //delegate to subclass:
      handleTransactionsBeforePhase(event);
      
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         afterRestoreView( facesContext );         
         beforeRender(event);
      }
      
      raiseEventsBeforePhase(event);

   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );
      
      raiseEventsAfterPhase(event);
      
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         afterRestoreView(facesContext);
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         afterInvokeApplication();
      }
      else if ( event.getPhaseId() == PROCESS_VALIDATIONS )
      {
         afterProcessValidations( event.getFacesContext() );
      }
      
      FacesMessages.afterPhase();
      
      //delegate to subclass:
      handleTransactionsAfterPhase(event);
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         //writeConversationIdToResponse( facesContext.getExternalContext().getResponse() );
         afterRender(facesContext);
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION || facesContext.getRenderResponse() || facesContext.getResponseComplete() )
      {
         Manager.instance().beforeRedirect();
         writeConversationIdToResponse( facesContext.getExternalContext().getResponse() );
         afterResponseComplete(facesContext);
      }

      Lifecycle.setPhaseId(null);
      
   }

   /**
    * Write out the conversation id as a servlet response header or portlet
    * render parameter.
    */
   private static void writeConversationIdToResponse(Object response)
   {
      Manager manager = Manager.instance();
      if ( manager.isLongRunningConversation() )
      {
         setPortletRenderParameter(
               response, 
               manager.getConversationIdParameter(), 
               manager.getCurrentConversationId()
            );
      }
   }

   private static void setPortletRenderParameter(Object response, String conversationIdParameter, String conversationId)
   {
      if (response instanceof ActionResponse)
      {
         ( (ActionResponse) response ).setRenderParameter(conversationIdParameter, conversationId);
      }
   }
   
   protected void handleTransactionsAfterPhase(PhaseEvent event) {}
   protected void handleTransactionsBeforePhase(PhaseEvent event) {}

}
