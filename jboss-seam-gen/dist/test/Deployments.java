package @testPackage@;

import java.io.File;

import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments {
   
   public static final String WEBAPP_SRC = "@workspaceHome@/@projectName@/test-build";
   
   public static WebArchive defaultDeployment() {
      
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.merge(ShrinkWrap.create(GenericArchive.class).as(ExplodedImporter.class)
          .importDirectory(WEBAPP_SRC).as(GenericArchive.class),
          "/", Filters.includeAll());
      for (File f : new File("@workspaceHome@/@projectName@/lib/").listFiles()) 
      {
         if (f.isFile())
         {
            war.addAsLibrary(f);
         }
      } 
      
      return war;
               
   }
   
}