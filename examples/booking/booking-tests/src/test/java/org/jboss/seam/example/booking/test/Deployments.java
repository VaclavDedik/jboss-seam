package org.jboss.seam.example.booking.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

public class Deployments {
   public static EnterpriseArchive bookingDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "seam-booking.ear").importFrom(new File("../booking-ear/target/seam-booking.ear"))
            .as(EnterpriseArchive.class);
   }
}
