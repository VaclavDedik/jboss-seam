package ${packageName};

import javax.ejb.Local;

@Local
public interface ${interfaceName} {  
   
	//seam-gen methods
	public String go(); 
	public void destroy();
	
   //add additional interface methods here
}