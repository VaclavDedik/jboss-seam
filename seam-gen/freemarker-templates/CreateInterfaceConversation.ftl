package ${packageName};

import javax.ejb.Local;

@Local
public interface ${interfaceName} {  
	
	//seam-gen methods
	public String begin();
	public String end();
	public void destroy();
	
   //add additional interface methods here	
}