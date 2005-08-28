//$Id$
package org.jboss.seam.test;

import java.util.HashMap;
import java.util.Map;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockHttpSession;
import org.jboss.seam.mock.MockLifecycle;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class PhaseListenerTest
{
   @Test
   public void testSeamPhaseListener()
   {
      ServletContext servletContext = new MockServletContext();
      HttpSession session = new MockHttpSession(servletContext);
      HttpServletRequest request = new MockHttpServletRequest( session );
      MockFacesContext facesContext = new MockFacesContext( request );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new WebApplicationContext(servletContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      
      SeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert facesContext.getViewRoot().getAttributes().size()==0;
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerLongRunning()
   {
      ServletContext servletContext = new MockServletContext();
      HttpSession session = new MockHttpSession(servletContext);
      HttpServletRequest request = new MockHttpServletRequest( session );
      MockFacesContext facesContext = new MockFacesContext( request );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new WebApplicationContext(servletContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      
      facesContext.getViewRoot().getAttributes().put(Manager.CONVERSATION_ID, "2");
      Map ids = new HashMap();
      ids.put("2", System.currentTimeMillis());
      new WebSessionContext(session).set(Manager.CONVERSATION_ID_MAP, ids);
      
      SeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert Manager.instance().isLongRunningConversation();
      
      facesContext.getViewRoot().getAttributes().clear();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert facesContext.getViewRoot().getAttributes().get(Manager.CONVERSATION_ID).equals("2");
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerNewLongRunning()
   {
      ServletContext servletContext = new MockServletContext();
      HttpSession session = new MockHttpSession(servletContext);
      HttpServletRequest request = new MockHttpServletRequest( session );
      MockFacesContext facesContext = new MockFacesContext( request );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new WebApplicationContext(servletContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );

      SeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      assert !Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      Manager.instance().setLongRunningConversation(true);
      
      assert Manager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !Manager.instance().isProcessInterceptors();
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

}
