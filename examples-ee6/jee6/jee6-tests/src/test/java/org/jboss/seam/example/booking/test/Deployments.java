package org.jboss.seam.example.booking.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

public class Deployments {
   public static WebArchive bookingDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "jboss-seam-jee6.war").importFrom(new File("../jee6-web/target/jboss-seam-jee6.war"))
            .as(WebArchive.class);
   }
}
