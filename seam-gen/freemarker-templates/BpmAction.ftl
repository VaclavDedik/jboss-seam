package ${bpmPackage};

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

public class ${actionName} implements ActionHandler {
	
	public void execute(ExecutionContext ctx) throws Exception {
		//your method implementation goes here
		ctx.leaveNode();		
	}
}
