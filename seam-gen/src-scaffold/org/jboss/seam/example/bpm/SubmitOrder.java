package org.jboss.seam.example.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class SubmitOrder implements ActionHandler {
	
	public void execute(ExecutionContext ctx) throws Exception {		
		System.out.println("ORDER SUBMITTED");
		ctx.leaveNode();		
	}
}
