package ${packageName};

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;

@Stateful
@Name("${actionName}")
@Scope(ScopeType.CONVERSATION)
public class ${actionName}Action implements ${actionName}, Serializable {
	
	//seam-gen method
	public String doAction()
	{
		//implement your business logic here
		System.out.println("Action Called");
		return "success";
	}
	
	//add additional action methods
	
	@Destroy @Remove                                                                      
	public void destroy() {}	
}
