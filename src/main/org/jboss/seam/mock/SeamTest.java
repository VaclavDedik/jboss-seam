/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.mock;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.transaction.UserTransaction;

import org.hibernate.validator.ClassValidator;
import org.hibernate.validator.InvalidValue;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.Manager;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.jsf.SeamNavigationHandler;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.jsf.SeamStateManager;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;
import org.jboss.seam.util.Validation;
import org.testng.annotations.Configuration;

/**
 * Superclass for TestNG integration tests for JSF/Seam applications.
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class SeamTest
{

   private MockExternalContext externalContext;
   private MockServletContext servletContext;
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
   
   /**
    * Script is an abstract superclass for usually anonymous  
    * inner classes that test JSF interactions.
    * 
    * @author Gavin King
    */
   public abstract class Script
   {
      private String conversationId;
      private String outcome;
      private boolean validationFailed;
      
      /**
       * A script for a JSF interaction with
       * no existing long-running conversation.
       */
      protected Script() {}
      
      /**
       * A script for a JSF interaction in the
       * scope of an existing long-running
       * conversation.
       */
      protected Script(String id)
      {
         conversationId = id;
      }
      
      protected boolean isGetRequest()
      {
         return false;
      }
      
      protected String getViewId()
      {
         return null;
      }
      
      /**
       * Helper method for resolving components in
       * the test script.
       */
      protected Object getInstance(Class clazz)
      {
         return Component.getInstance(clazz, true);
      }

      /**
       * Helper method for resolving components in
       * the test script.
       */
      protected Object getInstance(String name)
      {
         return Component.getInstance(name, true);
      }
      
      /**
       * Override to implement the interactions between
       * the JSF page and your components that occurs
       * during the apply request values phase.
       */
      protected void applyRequestValues() throws Exception {}
      /**
       * Override to implement the interactions between
       * the JSF page and your components that occurs
       * during the process validations phase.
       */
      protected void processValidations() throws Exception {}
      /**
       * Override to implement the interactions between
       * the JSF page and your components that occurs
       * during the update model values phase.
       */
      protected void updateModelValues() throws Exception {}
      /**
       * Override to implement the interactions between
       * the JSF page and your components that occurs
       * during the invoke application phase.
       */
      protected void invokeApplication() throws Exception {}
      protected void setOutcome(String outcome) { this.outcome = outcome; }
      protected String getInvokeApplicationOutcome() { return outcome; }
      /**
       * Override to implement the interactions between
       * the JSF page and your components that occurs
       * during the render response phase.
       */
      protected void renderResponse() throws Exception {}
      /**
       * Override to set up any request parameters for
       * the request.
       */
      protected void setup() {}
      
      public Map<String, String[]> getParameters()
      {
         return ( (MockHttpServletRequest) externalContext.getRequest() ).getParameters();
      }
      
      public Map<String, String[]> getHeaders()
      {
         return ( (MockHttpServletRequest) externalContext.getRequest() ).getHeaders();
      }
      
      protected void validate(Class modelClass, String property, Object value)
      {
         ClassValidator validator = Validation.getValidator(modelClass);
         InvalidValue[] ivs = validator.getPotentialInvalidValues(property, value);
         if (ivs.length>0)
         {
            validationFailed = true;
            FacesMessage message = FacesMessages.createFacesMessage( FacesMessage.SEVERITY_WARN, ivs[0].getMessage() );
            FacesContext.getCurrentInstance().addMessage( property, /*TODO*/ message );
            FacesContext.getCurrentInstance().renderResponse();
         }
      }
      
      public boolean isValidationFailure()
      {
         return validationFailed;
      }
      
      public String run() throws Exception
      {   
         facesContext = new MockFacesContext( externalContext, application );
         facesContext.setCurrent();
         Map attributes = facesContext.getViewRoot().getAttributes();
         
         if ( !isGetRequest() && conversationId!=null ) 
         {
            if ( conversationStates.containsKey( conversationId ) )
            {
               attributes.putAll(
                       conversationStates.get( conversationId ).state
                  );
            }
         }
                  
         setup();
         
         facesContext.getViewRoot().setViewId( getViewId() );

         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE) );
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE) );

         if ( !isGetRequest() )
         {
         
            phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE) );
            
            applyRequestValues();
      
            phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE) );
            phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE) );
            
            processValidations();
            
            phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE) );

            if ( !FacesContext.getCurrentInstance().getRenderResponse() )
            {
         
               phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE) );
               
               updateModelValues();
         
               phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE) );
               phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE) );
               
               invokeApplication();
               
               String outcome = getInvokeApplicationOutcome();
               facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
         
               phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE) );
               
            }
            
         }
         
         if ( !FacesContext.getCurrentInstance().getResponseComplete() )
         {
         
            phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE) );
            
            //TODO: hackish workaround for the fact that page actions don't get called!
            if ( isGetRequest() )
            {
               Lifecycle.setPhaseId(PhaseId.INVOKE_APPLICATION);
               invokeApplication();
               Lifecycle.setPhaseId(PhaseId.RENDER_RESPONSE);
            }
            
            renderResponse();
            
            facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
            
            phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
            
         }

         Map pageContextMap = (Map) attributes.get( ScopeType.PAGE.getPrefix() );
         if (pageContextMap!=null)
         {
            conversationId = (String) pageContextMap.get(Manager.CONVERSATION_ID);
            ConversationState conversationState = new ConversationState();
            conversationState.state.putAll( attributes );
            conversationStates.put( conversationId, conversationState );
         }
         
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
      if ( facesContext!=null )
      {
         Lifecycle.endSession( servletContext, new ServletSessionImpl( (HttpSession) facesContext.getExternalContext().getSession(true) ) );
      }
   }
   
   /**
    * Create a SeamPhaseListener by default. Override to use 
    * one of the other standard Seam phase listeners.
    */
   protected SeamPhaseListener createPhaseListener()
   {
	  return new SeamPhaseListener();
   }

   @Configuration(beforeTestClass=true)
   public void init() throws Exception
   {
      application = new MockApplication();
      application.setStateManager( new SeamStateManager( application.getStateManager() ) );
      application.setNavigationHandler( new SeamNavigationHandler( application.getNavigationHandler() ) );
      //don't need a SeamVariableResolver, because we don't test the view 
      phases = createPhaseListener();
      
      servletContext = new MockServletContext();
      initServletContext( servletContext.getInitParameters() );
      new Initialization(servletContext).init();
      Lifecycle.setServletContext(servletContext);

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
   
   /**
    * Override to set up any servlet context attributes.
    */
   public void initServletContext(Map initParams) {}

   private class ConversationState
   {
      Map state = new HashMap();
   }
   
   protected InitialContext getInitialContext() throws NamingException {
      return Naming.getInitialContext();
   }
   
   protected UserTransaction getUserTransaction() throws NamingException
   {
      return Transactions.getUserTransaction();
   }
   
   protected Object getField(Object object, String fieldName)
   {
      try
      {
         Field declaredField = object.getClass().getDeclaredField(fieldName);
         if ( !declaredField.isAccessible() ) declaredField.setAccessible(true);
         return declaredField.get(object);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not get field value: " + fieldName, e);
      }
   }

   protected void setField(Object object, String fieldName, Object value)
   {
      try
      {
         Field declaredField = object.getClass().getDeclaredField(fieldName);
         if ( !declaredField.isAccessible() ) declaredField.setAccessible(true);
         declaredField.set(object, value);
      }
      catch (Exception e)
      {
         throw new IllegalArgumentException("could not set field value: " + fieldName, e);
      }
   }

}
