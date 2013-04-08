package @testPackage@;

import org.junit.Test;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OverProtocol;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.seam.mock.JUnitSeamTest;
import org.junit.runner.RunWith;
import java.io.File;

@extraImports@

@RunWith(Arquillian.class)
public class @interfaceName@Test extends JUnitSeamTest {

   public static final String WEBAPP_SRC = "@workspaceHome@/@projectName@/test-build";
   public static final String PROJECT_HOME = "@workspaceHome@/@projectName@/";

   @Deployment(name="@interfaceName@Test")
   @OverProtocol("Servlet 3.0") 
   public static WebArchive createDeployment()
   {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
        war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
            .importDirectory(WEBAPP_SRC).as(GenericArchive.class), "/", Filters.includeAll())
            .addClasses(@interfaceName@.class, @beanName@.class)
            .addAsLibraries(@libraryList@)
            .addAsResource("seam.properties");
        return war;
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
