package @testPackage@;

import org.testng.annotations.Test;
import org.jboss.seam.mock.SeamTest;

public class @interfaceName@Test extends SeamTest {

	@Test
	public void test() throws Exception {
		new FacesRequest() {
			@Override
			protected void invokeApplication() {
				//call action methods here
				invokeMethod("#{@componentName@.@methodName@}");
			}
		}.run();
	}	
}
