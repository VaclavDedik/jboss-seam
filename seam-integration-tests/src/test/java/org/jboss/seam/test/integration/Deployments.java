package org.jboss.seam.test.integration;

import java.io.File;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments {
   public static WebArchive defaultSeamDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "test.war").importFrom(new File("target/seam-integration-tests.war")).as(WebArchive.class)
            .addAsWebInfResource(new StringAsset(
                  "<jboss-deployment-structure>" +
                        "<deployment>" +
                        "<dependencies>" +
                        "<module name=\"org.javassist\"/>" +
                        "<module name=\"org.dom4j\"/>" +
                        "</dependencies>" +
                        "</deployment>" +
                  "</jboss-deployment-structure>"), "jboss-deployment-structure.xml")
                  .addAsResource("seam.properties")
                  .addAsResource("components.properties")
                  .addAsResource("messages_en.properties")
                  .addAsResource("META-INF/persistence.xml")

                  .addAsResource("hibernate.cfg.xml")
                  .addAsWebInfResource("WEB-INF/components.xml", "components.xml")
                  .addAsWebInfResource("WEB-INF/pages.xml", "pages.xml")
                  .addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                  .addAsWebInfResource("WEB-INF/ejb-jar.xml", "ejb-jar.xml");
   }

   public static WebArchive jbpmSeamDeployment() {
      return ShrinkWrap.create(ZipImporter.class, "test.war").importFrom(new File("target/seam-integration-tests.war")).as(WebArchive.class)
            .addAsWebInfResource(new StringAsset(
                  "<jboss-deployment-structure>" +
                        "<deployment>" +
                        "<dependencies>" +
                        "<module name=\"org.javassist\"/>" +
                        "<module name=\"org.dom4j\"/>" +
                        "<module name=\"org.apache.commons.collections\"/>" +
                        "</dependencies>" +
                        "</deployment>" +
                  "</jboss-deployment-structure>"), "jboss-deployment-structure.xml")
                  .addAsResource("seam.properties")
                  .addAsResource("components.properties")
                  .addAsResource("messages_en.properties")
                  .addAsResource("META-INF/persistence.xml")

                  .addAsResource("testProcess1.jpdl.xml")
                  .addAsResource("testProcess2.jpdl.xml")
                  .addAsResource("testProcess3.jpdl.xml")
                  .addAsResource("testProcess4.jpdl.xml")

                  .addAsResource("jbpm.cfg.xml")

                  .addAsResource("hibernate.cfg.xml")
                  .addAsWebInfResource("WEB-INF/components-jbpm.xml", "components.xml")
                  .addAsWebInfResource("WEB-INF/pages.xml", "pages.xml")
                  .addAsWebInfResource("WEB-INF/web.xml", "web.xml")
                  .addAsWebInfResource("WEB-INF/ejb-jar.xml", "ejb-jar.xml");
   }
}
