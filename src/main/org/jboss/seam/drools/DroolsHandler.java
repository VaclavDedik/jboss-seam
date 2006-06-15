package org.jboss.seam.drools;

import java.util.List;

import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.core.Actor;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.el.ELException;

/**
 * Common functionality for jBPM handlers for Drools.
 * 
 * @author Jeff Delong
 * @author Gavin King
 *
 */
public class DroolsHandler
{
   protected WorkingMemory getWorkingMemory(String workingMemoryName, List<String> objectNames, ExecutionContext executionContext) 
         throws ELException
   {
      WorkingMemory workingMemory = (WorkingMemory) Component.getInstance(workingMemoryName, true);

      for (String objectName: objectNames)
      {
         //TODO: support EL expressions here:
         Object object = new SeamVariableResolver().resolveVariable(objectName);
         // assert the object into the rules engine
         workingMemory.assertObject(object);
      }
      
      workingMemory.setGlobal( "contextInstance", executionContext.getContextInstance() );
      workingMemory.assertObject( Actor.instance() );

      return workingMemory;
   }
}
