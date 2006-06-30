package org.jboss.seam.jsf;

import static javax.faces.event.PhaseId.ANY_PHASE;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.model.DataModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ContextAdaptor;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Pages;

public abstract class AbstractSeamPhaseListener implements PhaseListener
{
   
   private static final Log log = LogFactory.getLog(AbstractSeamPhaseListener.class);
   
   public PhaseId getPhaseId()
   {
      return ANY_PHASE;
   }

   /**
    * Restore the page and conversation contexts during a JSF request
    */
   public static void restoreAnyConversationContext(FacesContext facesContext)
   {
      Lifecycle.resumePage();
      Map parameters = facesContext.getExternalContext().getRequestParameterMap();
      boolean conversationFound = Manager.instance().restoreConversation(parameters);
      Lifecycle.resumeConversation( facesContext.getExternalContext() );
      if (!conversationFound)
      {
         redirectToNoConversationView();
      }
      if ( Init.instance().isJbpmInstalled() )
      {
         Pageflow.instance().validatePageflow();
      }
      Manager.instance().handleConversationPropagation(parameters);
      
      if ( log.isDebugEnabled() )
      {
         log.debug( "After restoring conversation context: " + Contexts.getConversationContext() );
      }
   }

   private static void redirectToNoConversationView()
   {
      FacesMessages.instance().addFromResourceBundle( 
            FacesMessage.SEVERITY_WARN, 
            "org.jboss.seam.NoConversation", 
            "No conversation" 
         );
      Manager.instance().redirect( Pages.instance().getNoConversationViewId() );
   }

   /**
    * Store the page and conversation contexts during a JSF request
    */
   public static void storeAnyConversationContext(FacesContext facesContext)
   {
      if ( !Contexts.isConversationContextActive() )
      {
         log.debug( "No active conversation context" );
      }
      else
      {
         Lifecycle.flushClientConversation();
         ContextAdaptor session = ContextAdaptor.getSession( facesContext.getExternalContext(), true );
         Manager.instance().storeConversation( session, facesContext.getExternalContext().getResponse() );
      }
      Lifecycle.flushPage();
   }
   
   /**
    * Look for a DataModel row selection in the request parameters,
    * and apply it to the DataModel.
    * 
    * @param parameters the request parameters
    */
   private static void selectDataModelRow(Map parameters)
   {
      String dataModelSelection = (String) parameters.get("dataModelSelection");
      if (dataModelSelection!=null)
      {
         int loc = dataModelSelection.indexOf('[');
         String name = dataModelSelection.substring(0, loc);
         int index = Integer.parseInt( dataModelSelection.substring( loc+1, dataModelSelection.length()-1 ) );
         Object value = Contexts.lookupInStatefulContexts(name);
         if (value!=null)
         {
            ( (DataModel) value ).setRowIndex(index);
         }
      }
   }

   protected void beforeRender(PhaseEvent event)
   {  
      FacesContext facesContext = event.getFacesContext();

      selectDataModelRow( facesContext.getExternalContext().getRequestParameterMap() );
      
      callPageActions(event);
      
      if ( facesContext.getResponseComplete() )
      {
         //if the page actions called responseComplete(),
         //we need to call beforeSaveState(), since the
         //component tree will not get rendered
         storeAnyConversationContext(facesContext);
         
         //workaround for a bug in MyFaces prior to 1.1.3
         if ( Init.instance().isMyFacesLifecycleBug() ) 
         {
            Lifecycle.endRequest( facesContext.getExternalContext() );
         }
      }
      else //if the page actions did not call responseComplete()
      {
         FacesMessages.instance().beforeRenderResponse();
         Manager.instance().prepareBackswitch(event);
      }
   }

   public boolean callPageActions(PhaseEvent event)
   {
      Lifecycle.setPhaseId( PhaseId.INVOKE_APPLICATION );
      boolean actionsWereCalled = false;
      try
      {
         actionsWereCalled = Pages.callAction( event.getFacesContext() ) || actionsWereCalled;
         actionsWereCalled = Pages.instance().callAction() || actionsWereCalled;
         return actionsWereCalled;
      }
      finally
      {
         Lifecycle.setPhaseId( PhaseId.RENDER_RESPONSE );
         if (actionsWereCalled) 
         {
            FacesMessages.afterPhase();
            afterPageActions(); //TODO: does it really belong in the finally?
         }
      }
   }
   
   protected void afterPageActions() {}
   
   private static boolean exists = false;
   
   protected AbstractSeamPhaseListener()
   {
      if (exists) log.warn("There should only be one Seam phase listener per application");
      exists=true;
   }
   
}
