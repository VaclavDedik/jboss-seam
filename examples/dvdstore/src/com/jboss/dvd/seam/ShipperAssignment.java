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
        System.out.println("jbpm assignment for assignable " + assignable);
        assignable.setActorId(SHIPPER);
        System.out.println("JBPM!!!!!!!!! assigning task to " + SHIPPER);
    }
}
