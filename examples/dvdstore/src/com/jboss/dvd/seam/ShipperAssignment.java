package com.jboss.dvd.seam;

import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.graph.exe.ExecutionContext;

public class ShipperAssignment
    implements AssignmentHandler
{
    public void assign(Assignable assignable, ExecutionContext executionContext) 
        throws Exception
    {
        assignable.setPooledActors( new String[] {"shipper"} );
    }
}
