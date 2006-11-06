package @actionPackage@;

import javax.ejb.Stateless;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Stateless
@Name("@componentName@")
public class @beanName@ implements @interfaceName@ {
	
    @Logger private Log log;
	
    @In(create=true) 
    FacesMessages facesMessages;
    
    //seam-gen method
	public String @methodName@()
	{
		//implement your business logic here
		log.info("@componentName@.@methodName@() action called");
		facesMessages.add("@methodName@");
		return "success";
	}
	
   //add additional action methods
	
}
