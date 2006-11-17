//$Id$
package org.jboss.seam.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.http.HttpSession;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.FacesApplicationContext;
import org.jboss.seam.contexts.WebSessionContext;
import org.jboss.seam.core.Conversation;
import org.jboss.seam.core.ConversationEntries;
import org.jboss.seam.core.Events;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.FacesPage;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pages;
import org.jboss.seam.jsf.AbstractSeamPhaseListener;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.jsf.SeamStateManager;
import org.jboss.seam.mock.MockApplication;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockFacesContext;
import org.jboss.seam.mock.MockLifecycle;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.testng.annotations.Test;

public class PhaseListenerTest
{
   private void installComponents(Context appContext)
   {
      appContext.set( Seam.getComponentName(Init.class), new Init() );
      installComponent(appContext, Manager.class);
      installComponent(appContext, ConversationEntries.class);
      installComponent(appContext, FacesPage.class);
      installComponent(appContext, Conversation.class);
      installComponent(appContext, FacesMessages.class);
      installComponent(appContext, Pages.class);
      installComponent(appContext, Events.class);
   }
   
   private void installComponent(Context appContext, Class clazz)
   {
      appContext.set( Seam.getComponentName(clazz) + ".component", new Component(clazz) );
   }
   
   private MockFacesContext createFacesContext()
   {
      ExternalContext externalContext = new MockExternalContext();
      MockFacesContext facesContext = new MockFacesContext( externalContext, new MockApplication() );
      facesContext.setCurrent().createViewRoot();
      facesContext.getApplication().setStateManager( new SeamStateManager( facesContext.getApplication().getStateManager() ) );
      
      Context appContext = new FacesApplicationContext(externalContext);
      installComponents(appContext);
      return facesContext;
   }
   
   @Test
   public void testSeamPhaseListener()
   {
      MockFacesContext facesContext = createFacesContext();
      AbstractSeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
            
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
            
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
      
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert ( (FacesPage) getPageMap(facesContext).get( getPrefix() + Seam.getComponentName(FacesPage.class) ) ).getConversationId()==null;
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   private String getPrefix()
   {
      return ScopeType.PAGE.getPrefix() + '$';
   }

   @Test
   public void testSeamPhaseListenerLongRunning()
   {
      MockFacesContext facesContext = createFacesContext();
      
      getPageMap(facesContext).put( getPrefix() + Seam.getComponentName(FacesPage.class), new FacesPage() { @Override public String getConversationId() { return "2"; } });
      
      List<String> conversationIdStack = new ArrayList<String>();
      conversationIdStack.add("2");
      ConversationEntries entries = new ConversationEntries();
      entries.createConversationEntry("2", conversationIdStack);
      WebSessionContext sessionContext = new WebSessionContext( new ServletSessionImpl( (HttpSession) facesContext.getExternalContext().getSession(true) ) );
      sessionContext.set( Seam.getComponentName(ConversationEntries.class), entries );
      
      AbstractSeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isConversationContextActive();
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      assert Manager.instance().isLongRunningConversation();
      
      facesContext.getViewRoot().getAttributes().clear();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );

      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
      
      assert ( (FacesPage) getPageMap(facesContext).get( getPrefix() + Seam.getComponentName(FacesPage.class) ) ).getConversationId().equals("2");

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   private Map getPageMap(MockFacesContext facesContext)
   {
      return facesContext.getViewRoot().getAttributes();
   }

   @Test
   public void testSeamPhaseListenerNewLongRunning()
   {
      MockFacesContext facesContext = createFacesContext();

      AbstractSeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      Manager.instance().beginConversation(null);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      assert Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      assert facesContext.getViewRoot().getAttributes().size()==1;

      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerRedirect()
   {
      MockFacesContext facesContext = createFacesContext();
      AbstractSeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE ) );
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE ) );
            
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      facesContext.responseComplete();
            
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE ) );
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

   @Test
   public void testSeamPhaseListenerNonFacesRequest()
   {
      MockFacesContext facesContext = createFacesContext();
      AbstractSeamPhaseListener phases = new SeamPhaseListener();

      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();

      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE ) );
      
      assert Contexts.isConversationContextActive();
      assert !Manager.instance().isLongRunningConversation();
      
      phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
      
      assert facesContext.getViewRoot().getAttributes().size()==1;
      assert ( (FacesPage) getPageMap(facesContext).get( getPrefix() + Seam.getComponentName(FacesPage.class) ) ).getConversationId()==null;
      assert Contexts.isEventContextActive();
      assert Contexts.isSessionContextActive();
      assert Contexts.isApplicationContextActive();
      assert Contexts.isConversationContextActive();
      
      facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
      
      phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
      
      assert !Contexts.isEventContextActive();
      assert !Contexts.isSessionContextActive();
      assert !Contexts.isApplicationContextActive();
      assert !Contexts.isConversationContextActive();
   }

}
