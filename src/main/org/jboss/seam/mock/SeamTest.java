/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpSession;

import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.testng.annotations.Configuration;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamTest
{

   private MockExternalContext externalContext;
   private MockServletContext servletContext;
   private MockLifecycle lifecycle;
   private MockApplication application;
   private SeamPhaseListener phases;
   private MockFacesContext facesContext;
   private Map<String, ConversationState> conversationStates;
   
   protected HttpSession getSession()
   {
      return (HttpSession) externalContext.getSession(true);
   }
   
   protected boolean isSessionInvalid()
   {
      return ( (MockHttpSession) getSession() ).isInvalid();
   }
   
   protected FacesContext getFacesContext()
   {
      return facesContext;
   }
   
   public abstract class Script
   {
      private String conversationId;
      
      protected Script() {}
      
      protected Script(String id)
      {
         conversationId = id;
      }

      protected void applyRequestValues() throws Exception {}
      protected void processValidations() throws Exception {}
      protected void updateModelValues() throws Exception {}
      protected void invokeApplication() throws Exception {}
      protected String getInvokeApplicationOutcome() { return null; }
      protected void renderResponse() throws Exception {}
      protected void setParameters() {}
      
      public Map getRequestParameterMap()
      {
         return externalContext.getRequestParameterMap();
      }
      
      public String run() throws Exception
      {   
         facesContext = new MockFacesContext( externalContext, application );
         facesContext.setCurrent();
         Map attributes = facesContext.getViewRoot().getAttributes();
         if (conversationId!=null) 
         {
            attributes.put(Manager.CONVERSATION_ID, conversationId);
         }
         else
         {
            attributes.remove(Manager.CONVERSATION_ID);
         }
         if ( conversationStates.containsKey( conversationId ) )
         {
            attributes.putAll(
                    conversationStates.get( conversationId ).state
            	);
         }
         
         setParameters();
         
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
         
         applyRequestValues();
   
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
         
         processValidations();
   
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
         
         updateModelValues();
   
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
         
         invokeApplication();
         
         String outcome = getInvokeApplicationOutcome();
         if ( Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() )
         {
            //TODO: re-enable, once we get a way to mock the expression evaluation
            //Pageflow.instance().navigate(facesContext, outcome);
         }
   
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
         
         renderResponse();
         
         facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
         
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

         conversationId = (String) attributes.get(Manager.CONVERSATION_ID);         

         ConversationState conversationState = new ConversationState();
         conversationState.state.putAll( attributes );
         conversationStates.put( conversationId, conversationState );

         return conversationId;
      }
      
   }
   
   @Configuration(beforeTestMethod=true)
   public void begin()
   {
      externalContext = new MockExternalContext(servletContext);
   }

   @Configuration(afterTestMethod=true)
   public void end()
   {
      Lifecycle.endSession( servletContext, new ServletSessionImpl( (HttpSession) facesContext.getExternalContext().getSession(true) ) );
   }
   
   protected SeamPhaseListener createPhaseListener()
   {
	  return new SeamPhaseListener();
   }

   @Configuration(beforeTestClass=true)
   public void init() throws Exception
   {
      application = new MockApplication();
      phases = createPhaseListener();
      servletContext = new MockServletContext();
      
      initServletContext( servletContext.getInitParameters() );
      //Contexts.beginApplication(servletContext);
      lifecycle = new MockLifecycle();
      new Initialization(servletContext).init();

      conversationStates = new HashMap<String, ConversationState>();
   }

   @Configuration(afterTestClass=true)
   public void cleanup() throws Exception
   {
      Lifecycle.endApplication(servletContext);
      externalContext = null;
      conversationStates.clear();
      conversationStates = null;
   }

   public void initServletContext(Map initParams) {}

   private class ConversationState
   {
      Map state = new HashMap();
   }
}
