//$Id$
package org.jboss.seam.test;

import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesApplicationContext;
import org.jboss.seam.contexts.Session;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockHttpServletRequest;
import org.jboss.seam.mock.MockLifecycle;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.testng.annotations.Test;

public class PhaseListenerTest
{
   @Test
   public void testSeamPhaseListener()
   {
      ExternalContext externalContext = new MockExternalContext();
      HttpServletRequest request = new MockHttpServletRequest(externalContext);
      MockFacesContext facesContext = new MockFacesContext( externalContext, new MockApplication() );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( 
            Seam.getComponentName(Conversation.class) + ".component", 
            new Component(Conversation.class) 
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
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
            
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
            
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert facesContext.getViewRoot().getAttributes().size()==0;
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerLongRunning()
   {
      ExternalContext externalContext = new MockExternalContext();
      MockFacesContext facesContext = new MockFacesContext( externalContext, new MockApplication() );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( 
            Seam.getComponentName(Conversation.class) + ".component", 
            new Component(Conversation.class) 
         );
      
      facesContext.getViewRoot().getAttributes().put(Manager.CONVERSATION_ID, "2");
      Map ids = new HashMap();
      ids.put("2", System.currentTimeMillis());
      new WebSessionContext( new ServletSessionImpl( (HttpSession) externalContext.getSession(true) ) ).set(Manager.CONVERSATION_ID_MAP, ids);
      
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
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert Manager.instance().isLongRunningConversation();
      
      facesContext.getViewRoot().getAttributes().clear();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert facesContext.getViewRoot().getAttributes().get(Manager.CONVERSATION_ID).equals("2");

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerNewLongRunning()
   {
      ExternalContext externalContext = new MockExternalContext();
      
      MockFacesContext facesContext = new MockFacesContext( externalContext, new MockApplication() );
      MockLifecycle lifecycle = new MockLifecycle();
      facesContext.setCurrent();
      
      Context appContext = new FacesApplicationContext(externalContext);
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      appContext.set( 
            Seam.getComponentName(Manager.class) + ".component", 
            new Component(Manager.class) 
         );
      appContext.set( 
            Seam.getComponentName(Conversation.class) + ".component", 
            new Component(Conversation.class) 
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
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, lifecycle ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, lifecycle ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, lifecycle ) );
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      Manager.instance().setLongRunningConversation(true);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
      
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      assert facesContext.getViewRoot().getAttributes().size()==1;

      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

}
