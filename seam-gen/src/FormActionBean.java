package @actionPackage@;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.log.Log;
import org.jboss.seam.core.FacesMessages;
import org.hibernate.validator.Length;

@Stateful 
@Name("@componentName@")
public class @beanName@ implements @interfaceName@ {

    @Logger private Log log;
    
    @In
    FacesMessages facesMessages;
    
    private String value;
	
	//seam-gen method
	public String @methodName@()
	{
		//implement your business logic here
		log.info("@componentName@.@methodName@() action called with: #0", value);
		facesMessages.add( "@methodName@ #0", (Object) value );
		return "success";
	}
	
	//add additional action methods
	
	@Length(max=10)
	public String getValue()
	{
		return value;
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	@Destroy @Remove                                                                      
	public void destroy() {}	
}
