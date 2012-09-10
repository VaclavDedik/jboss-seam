package @testPackage@;

import java.io.File;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

package org.jboss.seam.example.booking.test;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

public class Deployments {
   public static EnterpriseArchive bookingDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "${project.name}.${project.type}").importFrom(new File("${workspace.home/dist/${project.name}.${project.type}"))
            .as(EnterpriseArchive.class);
   }
}