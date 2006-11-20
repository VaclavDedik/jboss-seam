//$Id$
package org.jboss.seam.test;

import org.jboss.seam.Seam;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Manager;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InitializationTest
{
   @Test
   public void testInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      new Initialization(servletContext).init();

      assert !servletContext.getAttributes().isEmpty();
      assert servletContext.getAttributes().containsKey( Seam.getComponentName(Manager.class) + ".component" );
      assert servletContext.getAttributes().containsKey( Seam.getComponentName(Foo.class) + ".component" );
      assert !Contexts.isApplicationContextActive();
   }

   //TODO: write a test for components.xml
}

