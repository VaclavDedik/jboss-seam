package ${packageName};

import javax.ejb.Local;

@Local
public interface ${interfaceName} {  
   
	//seam-gen methods
	public String ${componentName}(); 
	public String getValue();
	public void setValue(String value);
	public void destroy();
	
   //add additional interface methods here
}