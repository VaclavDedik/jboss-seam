package org.jboss.seam.example.numberguess.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.io.File;

public class Deployments {
    public static EnterpriseArchive numberGuessDeployment() {
        EnterpriseArchive ear = ShrinkWrap
                .create(ZipImporter.class, "seam-numberguess.ear")
                .importFrom(new File("../numberguess-ear/target/seam-numberguess.ear"))
                .as(EnterpriseArchive.class);

        WebArchive web = ear.getAsType(WebArchive.class, "numberguess-web.war");
        web.delete("/WEB-INF/web.xml");
        web.addAsWebInfResource("web.xml");

        return ear;
    }
}
