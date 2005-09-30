package com.jboss.dvd.seam;

import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;
import org.jbpm.graph.exe.ExecutionContext;

public class ShipperAssignment
    implements AssignmentHandler
{
    static final String SHIPPER = "shipper";

    public void assign(Assignable assignable, ExecutionContext executionContext) 
        throws Exception
    {
        assignable.setActorId(SHIPPER);
        //assignable.setPooledActors(new String[] {SHIPPER});
    }
}
