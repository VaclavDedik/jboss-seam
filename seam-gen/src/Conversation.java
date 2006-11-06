package @actionPackage@;

import javax.ejb.Local;

@Local
public interface @interfaceName@ {  
	
	//seam-gen methods
	public String begin();
	public String increment();
	public String end();
	public int getValue();
	public void destroy();
	
   //add additional interface methods here	
}