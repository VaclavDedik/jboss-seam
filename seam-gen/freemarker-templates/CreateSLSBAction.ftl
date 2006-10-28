package ${packageName};

import java.io.Serializable;

import javax.ejb.Remove;
import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;

@Stateless
@Name("${actionName}")
public class ${actionName}Action implements ${actionName}, Serializable {
	
    //seam-gen method
	public String doAction()
	{
		return "success";
	}
	
   //add additional action methods
	
}
