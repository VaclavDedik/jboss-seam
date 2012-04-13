package org.jboss.seam.example.booking.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

import java.io.File;

public class Deployments {
   public static EnterpriseArchive iceFacesDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "seam-icefaces.ear").importFrom(new File("../icefaces-ear/target/seam-icefaces.ear"))
              .as(EnterpriseArchive.class);
   }
}
