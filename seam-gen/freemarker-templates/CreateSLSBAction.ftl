package ${packageName};

import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Stateless
@Name("${componentName}")
public class ${actionName}Action implements ${actionName} {
	
    @Logger private Log log;
	
    @In(create=true) 
    FacesMessages facesMessages;
    
    //seam-gen method
	public String ${componentName}()
	{
		//implement your business logic here
		log.info("${componentName}() action called");
		facesMessages.add("${componentName}");
		return "success";
	}
	
   //add additional action methods
	
}
