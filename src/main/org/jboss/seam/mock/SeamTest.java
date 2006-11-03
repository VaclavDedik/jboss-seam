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

import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIViewRoot;
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
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.FacesMessages;
import org.jboss.seam.core.FacesPage;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Manager;
import org.jboss.seam.core.Pageflow;
import org.jboss.seam.core.Validation;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.jsf.AbstractSeamPhaseListener;
import org.jboss.seam.jsf.SeamApplication11;
import org.jboss.seam.jsf.SeamNavigationHandler;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.jsf.SeamStateManager;
import org.jboss.seam.servlet.ServletSessionImpl;
import org.jboss.seam.util.Naming;
import org.jboss.seam.util.Transactions;
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
   private AbstractSeamPhaseListener phases;
   private MockHttpSession session;
   private Map<String, Map> conversationViewRootAttributes;
   private Map<String, Object> pageParameters = new HashMap<String, Object>();
   
   protected void setParameter(String name, String value)
   {
      getParameters().put( name, new String[] {value} );
   }
   
   protected void setPageParameter(String name, Object value)
   {
      pageParameters.put(name, value);
   }
   
   protected Map<String, String[]> getParameters()
   {
      return ( (MockHttpServletRequest) externalContext.getRequest() ).getParameters();
   }
   
   protected Map<String, String[]> getHeaders()
   {
      return ( (MockHttpServletRequest) externalContext.getRequest() ).getHeaders();
   }
   
   protected HttpSession getSession()
   {
      return (HttpSession) externalContext.getSession(true);
   }
   
   protected boolean isSessionInvalid()
   {
      return ( (MockHttpSession) getSession() ).isInvalid();
   }
   
   /**
    * Helper method for resolving components in
    * the test script.
    */
   protected Object getInstance(Class clazz)
   {
      return Component.getInstance(clazz);
   }
   
   /**
    * Helper method for resolving components in
    * the test script.
    */
   protected Object getInstance(String name)
   {
      return Component.getInstance(name);
   }
   
   /**
    * Is there a long running conversation associated
    * with the current request?
    */
   protected boolean isLongRunningConversation() {
      return Manager.instance().isLongRunningConversation();
   }
   
   /**
    * Search in all contexts
    */
   public Object lookup(String name) {
      return Contexts.lookupInStatefulContexts(name);
   }
   
   /**
    * @deprecated use FacesRequest or NonFacesRequest
    * @author Gavin King
    */
   public abstract class Script extends Request {
      public Script() {}
      public Script(String conversationId)
      {
         super(conversationId);
      }
   }
   
   /**
    * Script is an abstract superclass for usually anonymous  
    * inner classes that test JSF interactions.
    * 
    * @author Gavin King
    */
   abstract class Request
   {
      private String conversationId;
      private String outcome;
      private boolean validationFailed;
      private MockFacesContext facesContext;
      private String viewId;
      private boolean renderResponseBegun;
      private boolean renderResponseComplete;
      private boolean invokeApplicationBegun;
      private boolean invokeApplicationComplete;
      private Application application;
      
      /**
       * A script for a JSF interaction with
       * no existing long-running conversation.
       */
      protected Request() {}
      
      /**
       * A script for a JSF interaction in the
       * scope of an existing long-running
       * conversation.
       */
      protected Request(String conversationId)
      {
         this.conversationId = conversationId;
      }
      
      /**
       * Is this a non-faces request? Override
       * if it is.
       * 
       * @return false by default
       */
      protected boolean isGetRequest()
      {
         return false;
      }
      
      /**
       * The JSF view id of the form that is being submitted
       * or of the page that is being rendered in a non-faces
       * request.
       * (override if you need page actions to be called,
       * and page parameters applied)
       */
      protected String getViewId()
      {
         return viewId;
      }
      
      protected void setViewId(String viewId)
      {
         this.viewId = viewId;
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
      /**
       * Set the outcome of the INVOKE_APPLICATION phase
       */
      protected void setOutcome(String outcome) { this.outcome = outcome; }
      /**
       * Get the outcome of the INVOKE_APPLICATION phase
       */
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
       * 
       * @deprecated use beforeRequest()
       */
      protected void setup() {}
      /**
       * Make some assertions, after the end of the request.
       */
      protected void afterRequest() {}
      /**
       * Do anything you like, after the start of the request.
       * Especially, set up any request parameters for the 
       * request.
       */
      protected void beforeRequest() {
         setup();
      }
      /**
       * Assert the current view id
       * 
       * @param viewId the JSF view id
       */
      protected String getRenderedViewId()
      {
         if ( Init.instance().isJbpmInstalled() && Pageflow.instance().isInProcess() )
         {
            return Pageflow.instance().getPage().getViewId();
         }
         else
         {
            //TODO: not working right now, 'cos no mock navigation handler!
            return getFacesContext().getViewRoot().getViewId();
         }
      }
      
      /**
       * @deprecated use validateValue()
       */
      protected void validate(Class modelClass, String property, Object value)
      {
         ClassValidator validator = Validation.instance().getValidator(modelClass, null);
         InvalidValue[] ivs = validator.getPotentialInvalidValues(property, value);
         if (ivs.length>0)
         {
            validationFailed = true;
            FacesMessage message = FacesMessages.createFacesMessage( FacesMessage.SEVERITY_WARN, ivs[0].getMessage() );
            FacesContext.getCurrentInstance().addMessage( property, /*TODO*/ message );
            FacesContext.getCurrentInstance().renderResponse();
         }
      }
      
      /**
       * Did a validation failure occur during a call to validate()?
       */
      protected boolean isValidationFailure()
      {
         return validationFailed;
      }
      
      protected FacesContext getFacesContext()
      {
         return facesContext;
      }
      
      protected String getConversationId()
      {
         return conversationId;
      }
      
      /**
       * Evaluate (get) a value binding
       */
      protected Object getValue(String valueExpression)
      {
         return application.createValueBinding(valueExpression).getValue(facesContext);
      }

      /**
       * Set a value binding
       */
      protected void setValue(String valueExpression, Object value)
      {
         application.createValueBinding(valueExpression).setValue(facesContext, value);
      }
      
      /**
       * Validate the value against model-based constraints
       * return true if the value is valid
       */
      protected boolean validateValue(String valueExpression, Object value)
      {
         InvalidValue[] ivs = Validation.instance().validate(facesContext, valueExpression, value);
         if (ivs.length>0)
         {
            validationFailed = true;
            facesContext.addMessage( null, FacesMessages.createFacesMessage( FacesMessage.SEVERITY_WARN, ivs[0].getMessage() ) );
            return false;
         }
         else
         {
            return true;
         }
      }

      /**
       * Call a method binding
       */
      protected Object invokeMethod(String methodExpression)
      {
         return application.createMethodBinding(methodExpression, null).invoke(facesContext, null);
      }

      /**
       * @return the conversation id
       * @throws Exception to fail the test
       */
      public String run() throws Exception
      {   
         externalContext = new MockExternalContext(servletContext, session);
         application = new SeamApplication11(SeamTest.this.application);
         facesContext = new MockFacesContext( externalContext, application );
         facesContext.setCurrent();
         
         beforeRequest();

         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE) );
         
         UIViewRoot viewRoot = facesContext.getApplication().getViewHandler().createView( facesContext, getViewId() );
         facesContext.setViewRoot(viewRoot);
         Map viewRootAttributes = facesContext.getViewRoot().getAttributes();
         if ( conversationId!=null )
         {
            if ( isGetRequest() ) 
            {
               setParameter( Manager.instance().getConversationIdParameter(), conversationId );
               //TODO: what about conversationIsLongRunning????
            }
            else
            {
               if ( conversationViewRootAttributes.containsKey(conversationId) )
               {
                  //should really only do this if the view id matches (not really possible to implement)
                  Map state = conversationViewRootAttributes.get(conversationId);
                  viewRootAttributes.putAll(state);
               }
            }
         }
         if ( !isGetRequest() )
         {
            viewRootAttributes.putAll(pageParameters);
         }
         
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RESTORE_VIEW, MockLifecycle.INSTANCE) );
         
         if ( !isGetRequest() && !skipToRender() )
         {
         
            phases.beforePhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE) );
            
            applyRequestValues();
      
            phases.afterPhase( new PhaseEvent(facesContext, PhaseId.APPLY_REQUEST_VALUES, MockLifecycle.INSTANCE) );
            
            if ( !skipToRender() )
            {
            
               phases.beforePhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE) );
               
               processValidations();
               
               phases.afterPhase( new PhaseEvent(facesContext, PhaseId.PROCESS_VALIDATIONS, MockLifecycle.INSTANCE) );
   
               if ( !skipToRender() )
               {
            
                  phases.beforePhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE) );
                  
                  updateModelValues();
            
                  phases.afterPhase( new PhaseEvent(facesContext, PhaseId.UPDATE_MODEL_VALUES, MockLifecycle.INSTANCE) );
   
                  if ( !skipToRender() )
                  {
               
                     phases.beforePhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE) );
                  
                     invokeApplicationBegun = true;
                     
                     invokeApplication();
                     
                     invokeApplicationComplete = true;
                  
                     String outcome = getInvokeApplicationOutcome();
                     facesContext.getApplication().getNavigationHandler().handleNavigation(facesContext, null, outcome);
                     
                     viewId = getRenderedViewId();
            
                     phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, MockLifecycle.INSTANCE) );
                     
                  }
                  
               }
               
            }
            
         }
         
         if ( !skipRender() )
         {
         
            phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE) );
            
            renderResponseBegun = true;
            
            renderResponse();
            
            renderResponseComplete = true;
            
            facesContext.getApplication().getStateManager().saveSerializedView(facesContext);
            
            phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, MockLifecycle.INSTANCE ) );
            
         }
         
         afterRequest();

         Map attributes = viewRootAttributes;
         if (attributes!=null)
         {
            FacesPage facesPage = (FacesPage) attributes.get( Seam.getComponentName(FacesPage.class) );
            if (facesPage!=null)
            {
               conversationId = facesPage.getConversationId();
               Map conversationState = new HashMap();
               conversationState.putAll(attributes);
               conversationViewRootAttributes.put(conversationId, conversationState);
            }
         }

         return conversationId;
      }

      private boolean skipRender()
      {
         return FacesContext.getCurrentInstance().getResponseComplete();
      }

      private boolean skipToRender()
      {
         return FacesContext.getCurrentInstance().getRenderResponse() || 
               FacesContext.getCurrentInstance().getResponseComplete();
      }

      protected boolean isInvokeApplicationBegun()
      {
         return invokeApplicationBegun;
      }

      protected boolean isInvokeApplicationComplete()
      {
         return invokeApplicationComplete;
      }

      protected boolean isRenderResponseBegun()
      {
         return renderResponseBegun;
      }

      protected boolean isRenderResponseComplete()
      {
         return renderResponseComplete;
      }
      
   }
   
   public class NonFacesRequest extends Request
   {
      public NonFacesRequest() {}

      /**
       * @param viewId the view id to be rendered
       */
      public NonFacesRequest(String viewId)
      {
         setViewId(viewId);
      }

      /**
       * @param viewId the view id to be rendered
       * @param conversationId the conversation id
       */
      public NonFacesRequest(String viewId, String conversationId)
      {
         super(conversationId);
         setViewId(viewId);
      }

      @Override
      protected final boolean isGetRequest()
      {
         return true;
      }

      @Override
      protected final void applyRequestValues() throws Exception
      {
         throw new UnsupportedOperationException();
      }

      @Override
      protected final void processValidations() throws Exception
      {
         throw new UnsupportedOperationException();
      }

      @Override
      protected final void updateModelValues() throws Exception
      {
         throw new UnsupportedOperationException();
      }

   }

   public class FacesRequest extends Request
   {
      
      public FacesRequest() {}

      /**
       * @param viewId the view id of the form that was submitted
       */
      public FacesRequest(String viewId)
      {
         setViewId(viewId);
      }

      /**
       * @param viewId the view id of the form that was submitted
       * @param conversationId the conversation id
       */
      public FacesRequest(String viewId, String conversationId)
      {
         super(conversationId);
         setViewId(viewId);
      }

      @Override
      protected final boolean isGetRequest()
      {
         return false;
      }

   }

   @Configuration(beforeTestMethod=true)
   public void begin()
   {
      session = new MockHttpSession(servletContext);
   }

   @Configuration(afterTestMethod=true)
   public void end()
   {
      Lifecycle.endSession( servletContext, new ServletSessionImpl(session) );
      session = null;
   }
   
   /**
    * Create a SeamPhaseListener by default. Override to use 
    * one of the other standard Seam phase listeners.
    */
   protected AbstractSeamPhaseListener createPhaseListener()
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
      Lifecycle.setServletContext(servletContext);
      new Initialization(servletContext).init();

      conversationViewRootAttributes = new HashMap<String, Map>();
   }

   @Configuration(afterTestClass=true)
   public void cleanup() throws Exception
   {
      Lifecycle.endApplication(servletContext);
      externalContext = null;
      conversationViewRootAttributes = null;
   }
   
   /**
    * Override to set up any servlet context attributes.
    */
   public void initServletContext(Map initParams) {}
   
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
