/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.INVOKE_APPLICATION;
import static javax.faces.event.PhaseId.RENDER_RESPONSE;
import static javax.faces.event.PhaseId.RESTORE_VIEW;
import static javax.faces.event.PhaseId.APPLY_REQUEST_VALUES;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

   private static final Log log = LogFactory.getLog( SeamPortletPhaseListener.class );

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
         restoreAnyConversationContext( facesContext );         
         beforeRender(event);
      }
      else if ( event.getPhaseId()== APPLY_REQUEST_VALUES )
      {
         beforeUpdateModelValues(event);
      }

   }

   public void afterPhase(PhaseEvent event)
   {
      log.trace( "after phase: " + event.getPhaseId() );
      
      FacesContext facesContext = event.getFacesContext();
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         restoreAnyConversationContext(facesContext);
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
         Lifecycle.endRequest( facesContext.getExternalContext() );
      }
      else if ( event.getPhaseId() == INVOKE_APPLICATION || facesContext.getResponseComplete() )
      {
         Manager.instance().beforeRedirect();
         storeAnyConversationContext(facesContext);
         Lifecycle.endRequest( facesContext.getExternalContext() );
      }

      Lifecycle.setPhaseId(null);
      
   }

   protected void handleTransactionsAfterPhase(PhaseEvent event) {}
   protected void handleTransactionsBeforePhase(PhaseEvent event) {}

}
