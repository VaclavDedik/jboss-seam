package @testPackage@;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments {
   public static EnterpriseArchive defaultEarDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "@projectName@.ear").importFrom(new File("@workspaceHome@/@projectName@/test-build/@projectName@.ear"))
            .as(EnterpriseArchive.class);
   }
   
   public static WebArchive defaultWarDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "@projectName@.war").importFrom(new File("@workspaceHome@/@projectName@/test-build/@projectName@.war"))
            .as(WebArchive.class);
   }
}