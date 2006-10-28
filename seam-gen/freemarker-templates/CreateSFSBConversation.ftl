package ${packageName};

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.ScopeType;

@Stateful
@Name("${actionName}")
@Scope(ScopeType.CONVERSATION)
public class ${actionName}Action implements ${actionName}, Serializable {
	
	@Begin
	public String beginAction()
	{
       //implement your begin conversation business logic
	   return "success";
	}
	
	//add additional action methods that participate in this conversation
	
	@End
	public String endAction()
	{
        //implement your end conversation business logic
		return "success";
	}	
	
	@Destroy @Remove                                                                      
	public void destroy() {}	
}
