package org.jboss.seam.test.integration;

import org.jboss.seam.test.integration.bpm.SeamExpressionEvaluatorTestController;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Deployments {
	public static WebArchive defaultSeamDeployment() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
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
                .addAsWebInfResource("WEB-INF/ejb-jar.xml", "ejb-jar.xml")
                
                .addAsLibraries(DependencyResolvers.use(MavenDependencyResolver.class)
             		   .configureFrom("pom.xml")
             		   .goOffline()
             		   .artifact("org.jboss.seam:jboss-seam-jsf2:2.3.0-SNAPSHOT")
             		   .resolveAsFiles());
	}
	
	public static WebArchive jbpmSeamDeployment() {
		return ShrinkWrap.create(WebArchive.class, "test.war")
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
                .addAsWebInfResource("WEB-INF/ejb-jar.xml", "ejb-jar.xml")
                
                .addAsLibraries(DependencyResolvers.use(MavenDependencyResolver.class)
             		   .configureFrom("pom.xml")
             		   .goOffline()
             		   .artifact("org.jboss.seam:jboss-seam-jsf2:2.3.0-SNAPSHOT")
             		   .artifact("org.jbpm.jbpm3:jbpm-jpdl:3.2.10.SP3-seam2")
             		   		.exclusion("org.hibernate:hibernate-core")
             		   .resolveAsFiles());
	}
}
