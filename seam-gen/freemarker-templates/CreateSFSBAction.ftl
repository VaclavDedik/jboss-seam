<#assign pound = "#">
package ${packageName};

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
@Name("${componentName}")
public class ${actionName}Action implements ${actionName} {

    @Logger private Log log;
    
    @In(create=true) 
    FacesMessages facesMessages;
    
    private String value;
	
	//seam-gen method
	public String ${componentName}()
	{
		//implement your business logic here
		log.info("${componentName}() action called with: ${pound}0", value);
		facesMessages.add( "${componentName} ${pound}0", (Object) value );
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
