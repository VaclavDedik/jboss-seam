//$Id$
package org.jboss.seam.mock;

import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.testng.annotations.Configuration;

public class SeamTest
{
   private static EJB3StandaloneDeployer deployer;
   private MockServletContext servletContext;
   private MockLifecycle lifecycle;
   private SeamPhaseListener phases;
   private MockFacesContext facesContext;
   private MockHttpSession session;
   
   protected HttpSession getSession()
   {
      return session;
   }
   
   protected boolean isSessionInvalid()
   {
      return session.isInvalid();
   }
   
   protected ServletContext getServletContext()
   {
      return servletContext;
   }
   
   protected FacesContext getFacesContext()
   {
      return facesContext;
   }
   
   public abstract class Script
   {
      private MockHttpServletRequest request;
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
      protected void renderResponse() throws Exception {}
      
      public String run() throws Exception
      {   
   
         request = new MockHttpServletRequest( session );
         facesContext = new MockFacesContext( request );
         facesContext.setCurrent();
         facesContext.getViewRoot().getAttributes().put("org.jboss.seam.conversationId", conversationId);
         
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
   
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.INVOKE_APPLICATION, lifecycle ) );
         phases.beforePhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
         
         conversationId = (String) facesContext.getViewRoot().getAttributes().get("org.jboss.seam.conversationId");         
         renderResponse();
         
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
         
         return conversationId;
      }
      
      protected HttpServletRequest getRequest()
      {
         return request;
      }
   }
   
   @Configuration(beforeTestMethod=true)
   public void begin()
   {
      session = new MockHttpSession( servletContext );
   }

   @Configuration(afterTestMethod=true)
   public void end()
   {
      Contexts.endSession(session);
      session = null;
   }

   @Configuration(beforeTestClass=true)
   public void init() throws Exception
   {  
      phases = new SeamPhaseListener();
      servletContext = new MockServletContext();
      initServletContext( servletContext.getInitParameters() );
      //Contexts.beginApplication(servletContext);
      lifecycle = new MockLifecycle();
      new Initialization().init(servletContext);
   }
   
   @Configuration(afterTestClass=true)
   public void cleanup() throws Exception
   {
      Contexts.endApplication(servletContext);
      servletContext = null;
   }
   
   @Configuration(afterSuite=true)
   public void cleanupSuite() throws Exception
   {
      if (deployer==null) return;
      deployer.stop();
      deployer.destroy();
      deployer = null;
   }
   
   @Configuration(beforeSuite=true)
   public void initSuite() throws Exception
   {
      if (deployer!=null) return;
      EJB3StandaloneBootstrap.boot(null);

      deployer = new EJB3StandaloneDeployer();
      deployer.getArchivesByResource().add("META-INF/persistence.xml");

      // need to set the InitialContext properties that deployer will use
      // to initial EJB containers
      //deployer.setJndiProperties(getInitialContextProperties());

      deployer.create();
      deployer.start();
   }
   
   public void initServletContext(Map initParams) {}

}
