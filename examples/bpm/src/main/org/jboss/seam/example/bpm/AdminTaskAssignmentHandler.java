package org.jboss.seam.example.bpm;

import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author <a href="mailto:steve@hibernate.org">Steve Ebersole </a>
 */
public class AdminTaskAssignmentHandler implements AssignmentHandler
{
   // silly one that always assigns to the 'admin' user

   public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception
   {
      System.out.println( "************************************************" );
      System.out.println( "assigning task to admin user!" );
      System.out.println( "************************************************" );
      assignable.setActorId( "admin" );
   }
}
