//$Id$
package org.jboss.seam.test;

import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.init.Initialization;
import org.jboss.seam.mock.MockServletContext;
import org.testng.annotations.Test;

public class InitializationTest
{
   @Test
   public void testEmptyInitialization()
   {
      MockServletContext servletContext = new MockServletContext();
      new Initialization(servletContext).init();

      assert servletContext.getAttributes().size()==44;
      assert !Contexts.isApplicationContextActive();
   }

   //TODO: write a test for components.xml
}

