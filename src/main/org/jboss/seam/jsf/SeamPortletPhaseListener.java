/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;
import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.portlet.ActionResponse;

import org.jboss.seam.log.LogProvider;
import org.jboss.seam.log.Logging;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Manager;

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

   private static final LogProvider log = Logging.getLogProvider( SeamPortletPhaseListener.class );

   @Override
   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      //delegate to subclass:
      handleTransactionsBeforePhase(event);
      
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW || event.getPhaseId() == RENDER_RESPONSE )
      {
         Lifecycle.beginRequest( facesContext.getExternalContext() );
      }
      
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         afterRestoreView( facesContext );         
         beforeRender(event);
      }
      else if ( event.getPhaseId()== APPLY_REQUEST_VALUES )
      {
         beforeUpdateModelValues(event);
      }
      
      super.beforePhase(event);

   }

   @Override
   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );
      
      super.afterPhase(event);
      
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         afterRestoreView(facesContext);
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION )
      {
         afterInvokeApplication();
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
   /*private static void writeConversationIdToResponse(Object response)
   {
      Manager manager = Manager.instance();
      String conversationIdParameter = manager.getConversationIdParameter();
      String conversationId;
      if ( manager.isLongRunningConversation() )
      {
         conversationId = manager.getCurrentConversationId();
      }
      else if ( manager.isNestedConversation() )
      {
         conversationId = manager.getParentConversationId();
      }
      else
      {
         //nothing to set
         return;
      }
      //setResponseHeader(response, conversationIdParameter, conversationId);
      setPortletRenderParameter(response, conversationIdParameter, conversationId);
   }*/

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
         setPortletRenderParameter(
               response, 
               manager.getConversationIsLongRunningParameter(), 
               Boolean.toString(manager.isReallyLongRunningConversation())
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

   /*private static void setResponseHeader(Object response, String conversationIdParameter, String conversationId)
   {
      if (response instanceof HttpServletResponse)
      {
         ( (HttpServletResponse) response ).setHeader(conversationIdParameter, conversationId);
      }
   }*/
   
   protected void handleTransactionsAfterPhase(PhaseEvent event) {}
   protected void handleTransactionsBeforePhase(PhaseEvent event) {}

}
