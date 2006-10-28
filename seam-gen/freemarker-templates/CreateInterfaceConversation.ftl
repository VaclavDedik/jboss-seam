package ${packageName};

import javax.ejb.Local;

@Local
public interface ${interfaceName} {  
	
	//seam-gen methods
	public String beginAction();
	public String endAction();
	public void destroy();
	
   //add additional interface methods here	
}