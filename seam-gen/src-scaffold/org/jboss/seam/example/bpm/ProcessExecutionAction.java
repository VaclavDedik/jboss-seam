package org.jboss.seam.example.bpm;

import javax.ejb.Remove;
import javax.ejb.Stateless;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.CreateProcess;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Stateless
@Name("shoppingAction")
public class ProcessExecutionAction implements ProcessExecution {
	
	@In(create=true)
	ShoppingCartBean cartBean; 
	
	@CreateProcess(definition="submitOrderProcess")
	public String submitOrder() {
		return "home";
	}		
}
