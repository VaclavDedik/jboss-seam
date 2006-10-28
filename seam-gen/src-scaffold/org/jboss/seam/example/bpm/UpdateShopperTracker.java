package org.jboss.seam.example.bpm;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class UpdateShopperTracker implements ActionHandler {
	
	public void execute(ExecutionContext ctx) throws Exception {
		System.out.println("SHOPPING TRACKER UPDATED");
		ctx.leaveNode();		
	}
}
