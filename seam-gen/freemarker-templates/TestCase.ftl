package ${testPackageName};

import org.testng.annotations.Test;

public class ${actionName}Test extends BaseTest {

	@Test
	public void test() throws Exception {
		new Script() {
			@Override
			protected void updateModelValues() throws Exception {				
				//simulate form input to Java POJOs
				
				//create pojos and set POJO attributes
				System.out.println("simulate form input to Java POJOs");
			}
			@Override
			protected void invokeApplication() {
				//simulate HTTP request (POST)
				
				//call action methods here
				System.out.println("simulate HTTP request (POST)");
			}
			@Override
			protected void renderResponse() {
				//simulate HTTP response
				
				//check model attributes if needed
				System.out.println("simulate HTTP response");
			}
		}.run();
	}	
}
