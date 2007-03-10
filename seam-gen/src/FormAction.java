package @actionPackage@;

import javax.ejb.Local;

@Local
public interface @interfaceName@ {  
   
	//seam-gen methods
	public void @methodName@(); 
	public String getValue();
	public void setValue(String value);
	public void destroy();
	
   //add additional interface methods here
}