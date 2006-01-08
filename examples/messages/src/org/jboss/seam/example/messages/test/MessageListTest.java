//$Id$
package org.jboss.seam.example.messages.test;

import java.util.Map;

import org.jboss.seam.core.Init;
import org.jboss.seam.mock.SeamTest;

public class MessageListTest extends SeamTest
{

   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put(Init.COMPONENT_CLASSES, "org.jboss.seam.core.Ejb");
   }
   
}
