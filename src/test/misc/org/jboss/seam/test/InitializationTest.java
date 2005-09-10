//$Id$
package org.jboss.seam.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Init;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InitializationTest
{
   @Test
   public void testEmptyInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      new Initialization(servletContext).setScannerEnabled(false).init();
      assert servletContext.getAttributes().size()==4;
      assert !Contexts.isApplicationContextActive();
   }

   @Test
   public void testInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      servletContext.getInitParameters().put(Init.COMPONENT_CLASS_NAMES, "org.jboss.seam.test.Foo, org.jboss.seam.test.Bar");
      servletContext.getInitParameters().put(Init.PERSISTENCE_UNIT_NAMES, "bookingDatabase");
      new Initialization(servletContext).setScannerEnabled(false).init();
      assert servletContext.getAttributes().size()==7;
      assert !Contexts.isApplicationContextActive();
   }
}
