package org.jboss.seam.example.quartz.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import java.io.File;

public class Deployments {
    public static EnterpriseArchive quartzDeployment() {
        EnterpriseArchive ear = ShrinkWrap.create(ZipImporter.class, "seam-quartz.ear").importFrom(new File("../quartz-ear/target/seam-quartz.ear"))
              .as(EnterpriseArchive.class);

        // Install org.jboss.seam.mock.MockSeamListener
        WebArchive web = ear.getAsType(WebArchive.class, "quartz-web.war");
        web.delete("/WEB-INF/web.xml");
        web.addAsWebInfResource("web.xml");
        
        web.addAsResource("BaseData.xml", "BaseData.xml");
        
        web.delete("/WEB-INF/components.xml");
        web.addAsWebInfResource("WEB-INF/components.xml", "components.xml");
        
        JavaArchive ejb =  ear.getAsType(JavaArchive.class, "quartz-ejb.jar");
        ejb.addClasses(TestPaymentController.class, TestPaymentProcessor.class, TransactionStatus.class);
        
        ear.addAsLibraries(DependencyResolvers.use(MavenDependencyResolver.class)
              .configureFrom("pom.xml")
              .artifact("org.dbunit:dbunit:jar:2.2")
              .resolveAsFiles());

        return ear;
    }
}
