package org.jboss.seam.example.booking.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
   public static EnterpriseArchive iceFacesDeployment() {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-icefaces.ear").importFrom(new File("../icefaces-ear/target/seam-icefaces.ear"))
              .as(EnterpriseArchive.class);
      
      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "icefaces-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      return ear;
   }
}
