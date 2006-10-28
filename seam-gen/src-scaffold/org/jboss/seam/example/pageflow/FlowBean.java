package org.jboss.seam.example.pageflow;

import org.jboss.seam.annotations.Name;

@Name("flowBean")
public class FlowBean {
	
	private boolean continueFlow;
	
	public FlowBean() {
		this.continueFlow = true;
	}
	
	public boolean getContinueFlow() {
		return this.continueFlow;
	}
	
	public void setContinueFlow(boolean aContinueFlow) {
		this.continueFlow = aContinueFlow;
	}	
}
