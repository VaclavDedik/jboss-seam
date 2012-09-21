package org.jboss.seam.example.hibernate.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
    public static WebArchive hibernateDeployment() {
       WebArchive web = ShrinkWrap.create(ZipImporter.class, "jboss-seam-hibernate.war").importFrom(new File("../hibernate-web/target/jboss-seam-hibernate.war"))
              .as(WebArchive.class);

        // Install org.jboss.seam.mock.MockSeamListener
        web.delete("/WEB-INF/web.xml");
        web.addAsWebInfResource("web.xml");
        
        return web;
    }
}
