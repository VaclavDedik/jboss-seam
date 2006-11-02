<#assign pound = "#">
package ${testPackageName};

import org.testng.annotations.Test;
import org.jboss.seam.mock.SeamTest;

public class ${actionName}Test extends SeamTest {

	@Test
	public void test() throws Exception {
		new FacesRequest() {
			@Override
			protected void updateModelValues() throws Exception {				
				//set form input to model attributes
			}
			@Override
			protected void invokeApplication() {
				//simulate HTTP request (POST)
				
				//call action methods here
				invokeMethod("${pound}{${actionName}.go}");
			}
			@Override
			protected void renderResponse() {
				//check model attributes if needed
			}
		}.run();
	}	
}
