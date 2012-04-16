package org.jboss.seam.example.jpa.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
   public static WebArchive jpaDeployment() {
      WebArchive web = ShrinkWrap.create(ZipImporter.class, "jboss-seam-jpa.war").importFrom(new File("../jpa-web/target/jboss-seam-jpa.war"))
            .as(WebArchive.class);

      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      return web;
   }
}
