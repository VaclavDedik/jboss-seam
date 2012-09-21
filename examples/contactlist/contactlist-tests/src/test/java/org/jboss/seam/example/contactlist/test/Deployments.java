package org.jboss.seam.example.contactlist.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

import java.io.File;

public class Deployments {
   public static EnterpriseArchive contactListDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "seam-contactlist.ear").importFrom(new File("../contactlist-ear/target/seam-contactlist.ear"))
              .as(EnterpriseArchive.class);
   }
}