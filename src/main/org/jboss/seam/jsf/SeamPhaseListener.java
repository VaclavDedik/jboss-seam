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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;

/**
 * Manages the Seam contexts associated with a JSF request.
 * 
 * Manages the thread/context associations throughoutt the
 * lifecycle of the JSF request.
 *
 * @author Gavin King
 */
public class SeamPhaseListener extends AbstractSeamPhaseListener
{

   private static final Log log = LogFactory.getLog(SeamPhaseListener.class);
   
   @Override
   public void beforePhase(PhaseEvent event)
   {
      log.trace( "before phase: " + event.getPhaseId() );
      
      Lifecycle.setPhaseId( event.getPhaseId() );

      //delegate to subclass:
      handleTransactionsBeforePhase(event);
      
      if ( event.getPhaseId() == RESTORE_VIEW )
      {
         Lifecycle.beginRequest( event.getFacesContext().getExternalContext() );
      }
      else if ( event.getPhaseId() == RENDER_RESPONSE )
      {
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
            
      //has to happen after, since restoreAnyConversationContext() 
      //can add messages
      FacesMessages.afterPhase();
      
      //delegate to subclass:
      handleTransactionsAfterPhase(event);
            
      if ( event.getPhaseId() == RENDER_RESPONSE )
      {
         afterRender(facesContext);
      }
      else if ( facesContext.getResponseComplete() )
      {
         afterResponseComplete(facesContext);
      }
      
      Lifecycle.setPhaseId(null);
      
   }

   protected void handleTransactionsAfterPhase(PhaseEvent event) {}
   protected void handleTransactionsBeforePhase(PhaseEvent event) {}

}
