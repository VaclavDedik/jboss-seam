package ${packageName};

import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;

@Stateless
@Name("${componentName}")
public class ${actionName}Action implements ${actionName} {
	
    @Logger private Log log;
	
    //seam-gen method
	public String ${componentName}()
	{
		//implement your business logic here
		log.info("${componentName}() action called");
		return "success";
	}
	
   //add additional action methods
	
}
