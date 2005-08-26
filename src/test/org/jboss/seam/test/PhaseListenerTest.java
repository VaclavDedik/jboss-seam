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
import org.jboss.seam.components.ConversationManager;
import org.jboss.seam.components.Settings;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.WebApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
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
      
      new WebApplicationContext(servletContext).set( 
            Seam.getComponentName(ConversationManager.class) + ".component",
            new Component(ConversationManager.class)
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
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !ConversationManager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      assert !ConversationManager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
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
      
      new WebApplicationContext(servletContext).set( 
            Seam.getComponentName(ConversationManager.class) + ".component",
            new Component(ConversationManager.class)
         );
      
      Settings settings = new Settings();
      settings.setConversationTimeout(10000);
      new WebApplicationContext(servletContext).set( 
            Seam.getComponentName(Settings.class),
            settings
         );
      
      facesContext.getViewRoot().getAttributes().put("org.jboss.seam.conversationId", "2");
      Map ids = new HashMap();
      ids.put("2", System.currentTimeMillis());
      new WebSessionContext(session).set("org.jboss.seam.allConversationIds", ids);
      
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
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert ConversationManager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      assert ConversationManager.instance().isLongRunningConversation();
      
      facesContext.getViewRoot().getAttributes().clear();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert facesContext.getViewRoot().getAttributes().get("org.jboss.seam.conversationId").equals("2");
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
      
      new WebApplicationContext(servletContext).set( 
            Seam.getComponentName(ConversationManager.class) + ".component",
            new Component(ConversationManager.class)
         );
      
      Settings settings = new Settings();
      settings.setConversationTimeout(10000);
      new WebApplicationContext(servletContext).set( 
            Seam.getComponentName(Settings.class),
            settings
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
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !ConversationManager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      ConversationManager.instance().setLongRunningConversation(true);
      
      assert ConversationManager.instance().isProcessInterceptors();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
      assert ConversationManager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert !ConversationManager.instance().isProcessInterceptors();
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
