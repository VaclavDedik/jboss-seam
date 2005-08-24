//$Id$
package org.jboss.seam.mock;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;

import org.jboss.ejb3.embedded.EJB3StandaloneBootstrap;
import org.jboss.ejb3.embedded.EJB3StandaloneDeployer;
import org.jboss.seam.components.Settings;
import org.jboss.seam.example.booking.Booking;
import org.jboss.seam.example.booking.Hotel;
import org.jboss.seam.example.booking.HotelBookingAction;
import org.jboss.seam.example.booking.User;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.jsf.SeamPhaseListener;
import org.jboss.seam.util.Strings;
import org.testng.annotations.Configuration;

public class SeamTest
{
   private EJB3StandaloneDeployer deployer;
   private MockServletContext mockServletContext;
   private MockLifecycle lifecycle;
   private SeamPhaseListener phases;
   private FacesContext facesContext;
   
   public abstract class Script
   {
      protected void applyRequestValues() throws Exception {}
      protected void processValidations() throws Exception {}
      protected void updateModelValues() throws Exception {}
      protected void invokeApplication() throws Exception {}
      protected void renderResponse() throws Exception {}
      
      public void run() throws Exception
      {   
   
         facesContext = new MockFacesContext( new MockHttpServletRequest( new MockHttpSession( mockServletContext ) ) );
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
         
         renderResponse();
         
         phases.afterPhase( new PhaseEvent(facesContext, PhaseId.RENDER_RESPONSE, lifecycle ) );
      }
   }

   @Configuration(afterTestClass=true)
   public void cleanup() throws Exception
   {
      deployer.stop();
      deployer.destroy();
   }
   
   @Configuration(beforeTestClass=true)
   public void init() throws Exception
   {
      EJB3StandaloneBootstrap.boot("");

      deployer = new EJB3StandaloneDeployer();
      deployer.getArchivesByResource().add("META-INF/persistence.xml");

      // need to set the InitialContext properties that deployer will use
      // to initial EJB containers
      //deployer.setJndiProperties(getInitialContextProperties());

      deployer.create();
      deployer.start();
      
      phases = new SeamPhaseListener();
      mockServletContext = new MockServletContext();
      mockServletContext.getInitParameters().put(Settings.PERSISTENCE_UNIT_NAMES, "bookingDatabase");
      String classNames = Strings.toString(HotelBookingAction.class, User.class, Booking.class, Hotel.class);
      mockServletContext.getInitParameters().put(Settings.COMPONENT_CLASS_NAMES, classNames);
      lifecycle = new MockLifecycle();
      new Initialization().init(mockServletContext);
   }

}
