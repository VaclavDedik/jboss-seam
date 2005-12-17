//$Id$
package org.jboss.seam.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockExternalContext;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InitializationTest
{
   @Test
   public void testEmptyInitialization()
   {
      MockExternalContext externalContext = new MockExternalContext();
      new Initialization(externalContext).setScannerEnabled(false).init();
      assert ((MockServletContext)externalContext.getContext()).getAttributes().size()==5 + 2*8;
      assert !Contexts.isApplicationContextActive();
   }

   @Test
   public void testInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      servletContext.getInitParameters().put(Init.COMPONENT_CLASSES, "org.jboss.seam.test.Foo, org.jboss.seam.test.Bar");
      servletContext.getInitParameters().put(Init.MANAGED_PERSISTENCE_CONTEXTS, "bookingDatabase");
      MockExternalContext externalContext = new MockExternalContext(servletContext);
      new Initialization(externalContext).setScannerEnabled(false).init();
      assert servletContext.getAttributes().size()==8 + 2*8;
      assert !Contexts.isApplicationContextActive();
   }
}
