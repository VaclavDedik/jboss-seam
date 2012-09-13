package @testPackage@;

import org.junit.Test;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.mock.JUnitSeamTest;
import @testPackage@.Deployments;
import @actionPackage@.@interfaceName@;
//import @actionPackage@.@beanName@;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class @interfaceName@Test extends JUnitSeamTest {

   @Deployment(name="@interfaceName@Test")
   @OverProtocol("Servlet 3.0") 
   public static WebArchive createDeployment()
   {
//      return Deployments.defaultDeployment()
//            .addClasses(@interfaceName@.class, @beanName@.class)
//            .addAsResource("seam.properties");
   }
   
   
	@Test
	public void test_@methodName@() throws Exception {
		new FacesRequest("/@pageName@.xhtml") {
			@Override
			protected void invokeApplication() {
				//call action methods here
				invokeMethod("#{@componentName@.@methodName@}");
			}
		}.run();
	}
}
