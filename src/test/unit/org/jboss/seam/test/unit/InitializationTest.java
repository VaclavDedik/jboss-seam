//$Id$
package org.jboss.seam.test.unit;

import org.jboss.seam.Component;
import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.contexts.ServletLifecycle;
import org.jboss.seam.core.Manager;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.jboss.seam.test.unit.component.ConfigurableComponent;
import org.jboss.seam.test.unit.component.PrimaryColor;
import org.testng.annotations.Test;

public class InitializationTest
{
   @Test
   public void testInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      ServletLifecycle.beginApplication(servletContext);
      new Initialization(servletContext).create().init();

      assert !servletContext.getAttributes().isEmpty();
      assert servletContext.getAttributes().containsKey( Seam.getComponentName(Manager.class) + ".component" );
      assert servletContext.getAttributes().containsKey( Seam.getComponentName(Foo.class) + ".component" );
      assert !Contexts.isApplicationContextActive();
      ServletLifecycle.endApplication();
   }

   /**
    * Configuration for ConfigurableComponent is defined in ConfigurableComponent.component.xml
    */
   @Test
   public void testEnumPropertyAssignment()
   {
       MockServletContext servletContext = new MockServletContext();
       ServletLifecycle.beginApplication(servletContext);
       new Initialization( servletContext ).create().init();

       Lifecycle.beginCall();

       ConfigurableComponent component = (ConfigurableComponent) Component.getInstance(ConfigurableComponent.class);
       assert component != null;
       assert component.getPrimaryColor().equals(PrimaryColor.RED);

       ServletLifecycle.endApplication();
   }
   //TODO: write a test for components.xml
}

