package @actionPackage@;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;

@Name("@componentName@")
public class @interfaceName@ {
	
    @Logger private Log log;
	
    @In FacesMessages facesMessages;
    
    public void @methodName@()
    {
        //implement your business logic here
        log.info("@componentName@.@methodName@() action called");
        facesMessages.add("@methodName@");
    }
	
   //add additional action methods
	
}
