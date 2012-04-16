package org.jboss.seam.example.nestedbooking.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
   public static EnterpriseArchive nestedBookingDeployment() {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-nestedbooking.ear").importFrom(new File("../nestedbooking-ear/target/seam-nestedbooking.ear"))
              .as(EnterpriseArchive.class);
      
      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "nestedbooking-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      return ear;
   }
}
