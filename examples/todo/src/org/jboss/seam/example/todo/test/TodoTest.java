//$Id$
package org.jboss.seam.example.todo.test;

import java.util.Map;

import org.jboss.seam.core.Init;
import org.jboss.seam.core.Jbpm;
import org.jboss.seam.mock.SeamTest;

public class TodoTest extends SeamTest
{
   
   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Jbpm");
      initParams.put(Jbpm.PROCESS_DEFINITIONS, "todo.jpdl.xml");
   }
   
}
