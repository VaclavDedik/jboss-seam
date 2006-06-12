package org.jboss.seam.drools;

import java.util.List;

import org.drools.WorkingMemory;
import org.jboss.seam.Component;
import org.jboss.seam.core.Actor;
import org.jboss.seam.jbpm.SeamVariableResolver;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

/**
 * A jBPM DecisionHandler that delegates to a Drools WorkingMemory
 * held in a Seam context variable. The decision outcome is returned
 * by setting the outcome attribute of the global named "decision".
 * 
 * @author Gavin King
 *
 */
public class DroolsDecisionHandler implements DecisionHandler
{

   public List<String> assertObjects;
   public String workingMemoryName;

   /**
    * The FireRulesActionHandler gets variables from the Instance, and asserts
    * them into the Rules Engine and invokes the rules.
    */
   public String decide(ExecutionContext executionContext) throws Exception
   {
      WorkingMemory workingMemory = (WorkingMemory) Component.getInstance(workingMemoryName, true);

      // load the facts
      for (String objectName: assertObjects)
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
      workingMemory.setGlobal( "decision", new Decision() );
      workingMemory.fireAllRules();
      return ( (Decision) workingMemory.getGlobal("decision") ).getOutcome();
   }
   
}