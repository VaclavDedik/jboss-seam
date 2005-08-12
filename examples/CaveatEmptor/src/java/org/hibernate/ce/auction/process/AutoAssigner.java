package org.hibernate.ce.auction.process;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.AssignmentHandler;
import org.jbpm.taskmgmt.exe.Assignable;


public class AutoAssigner implements AssignmentHandler {

  private static final long serialVersionUID = 1L;

  public void assign(Assignable assignable, ExecutionContext executionContext) throws Exception {
    assignable.setActorId("admin");
  }

}
