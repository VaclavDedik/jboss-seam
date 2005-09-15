package org.jboss.seam.test.bpm;

import java.util.Map;
import org.jboss.seam.mock.SeamTest;
import org.jboss.seam.core.Init;
import org.jboss.seam.core.Ejb;

public class JbpmComponentTests extends SeamTest
{
   @Override
   public void initServletContext(Map initParams)
   {
      initParams.put( Init.MANAGED_PERSISTENCE_CONTEXTS, "seamJbpmDatabase" );
      initParams.put(Init.COMPONENT_CLASSES, Ejb.class.getName());
      initParams.put( Init.JBPM_SESSION_FACTORY_NAME, "/JbpmSessionFactory" );
   }


}
