package org.jboss.seam.example.pageflow;

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateful
@Name("simpleFlow")
@Scope(ScopeType.CONVERSATION)
public class SimpleFlowAction implements SimpleFlow, Serializable {
	
	@In(create=true)
	private FlowBean flowBean;
		
	@Create  
	@Begin(pageflow="shellflow")
	public void begin()	{
		System.out.println("SHELLFLOW METHOD HIT!!!");		
	}
	
	public String gotoFirstPage() {
		return "first-page";
	}
		
	public String gotoSecondPage() {
		return "second-page";
	}
	
	public String gotoThirdPage() {
		return "third-page";
	}		
	
	public String continueFlowDecision() {
		System.out.println("Deferring to jBPM for flow decision");		
		return "continue-flow";
	}
	
	@Destroy @Remove                                                                      
	public void destroy() {}	
}
