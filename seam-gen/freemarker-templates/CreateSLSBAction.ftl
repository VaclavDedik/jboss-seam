package ${packageName};

import javax.ejb.Remove;
import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

@Stateless
@Name("${componentName}")
public class ${actionName}Action implements ${actionName} {
	
    @Logger private Log log;
	
    //seam-gen method
	public String go()
	{
		//implement your business logic here
		log.info("go() action called");
		return "success";
	}
	
   //add additional action methods
	
}
