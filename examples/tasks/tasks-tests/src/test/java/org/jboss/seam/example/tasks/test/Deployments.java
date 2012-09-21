package org.jboss.seam.example.tasks.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;
import org.jboss.shrinkwrap.api.asset.StringAsset;

public class Deployments {
   public static EnterpriseArchive tasksDeployment() {
      EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-tasks.ear").importFrom(new File("../tasks-ear/target/seam-tasks.ear"))
              .as(EnterpriseArchive.class);
      
      // Install org.jboss.seam.mock.MockSeamListener
      WebArchive web = ear.getAsType(WebArchive.class, "tasks-web.war");
      web.delete("/WEB-INF/web.xml");
      web.addAsWebInfResource("web.xml");
      
      return ear;
   }
}
