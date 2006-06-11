package org.jboss.seam.drools;

import java.util.List;

import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.core.Actor;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * A jBPM ActionHandler that delegates to a Drools WorkingMemory
 * held in a Seam context variable.
 * 
 * @author Jeff Delong
 * @author Gavin King
 *
 */
public class DroolsActionHandler implements ActionHandler
{

   public List<String> objectNames;
   public String workingMemoryName;

   /**
    * The FireRulesActionHandler gets variables from the Instance, and asserts
    * them into the Rules Engine and invokes the rules.
    */
   public void execute(ExecutionContext executionContext) throws Exception
   {
      WorkingMemory workingMemory = (WorkingMemory) Component.getInstance(workingMemoryName, true);

      // load the facts
      for (String objectName: objectNames)
      {
         //TODO: support EL expressions here:
         Object object = new SeamVariableResolver().resolveVariable(objectName);
         // assert the object into the rules engine
         workingMemory.assertObject(object);
      }
      
      // assert the contextInstance so that it may be used to set results
      // TODO: any other useful objects?
      workingMemory.setGlobal( "contextInstance", executionContext.getContextInstance() );
      workingMemory.assertObject( Actor.instance() );
      workingMemory.fireAllRules();
   }
   
}