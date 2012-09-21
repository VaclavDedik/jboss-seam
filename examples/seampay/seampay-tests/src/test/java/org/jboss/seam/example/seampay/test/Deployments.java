package org.jboss.seam.example.seampay.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
   public static EnterpriseArchive seamPayDeployment() {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-seampay.ear").importFrom(new File("../seampay-ear/target/seam-seampay.ear"))
              .as(EnterpriseArchive.class);
      
      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "seampay-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      return ear;
   }
}
