// $Id$
package org.jboss.seam.test.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * Implementation of JbpmTransitionListener.
 *
 * @author Steve Ebersole
 */
public class JbpmTransitionListener implements ActionHandler
{
   public void execute(ExecutionContext executionContext) throws Exception
   {
      String from = executionContext.getTransition().getFrom().getName();
      String to = executionContext.getTransition().getTo().getName();
      String transition = executionContext.getTransition().getName();
      System.out.println( "************************************************" );
      System.out.println( "jBPM executing transition [" + transition + "]" );
      System.out.println( "      " + from + " --> " + to );
      System.out.println( "************************************************" );
   }
}
