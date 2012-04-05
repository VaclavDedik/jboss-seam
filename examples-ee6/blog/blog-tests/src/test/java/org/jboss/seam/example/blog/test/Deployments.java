package org.jboss.seam.example.blog.test;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;

import java.io.File;

public class Deployments {
	public static EnterpriseArchive bookingDeployment() {
		return ShrinkWrap.create(ZipImporter.class, "seam-blog.ear").importFrom(new File("../blog-ear/target/seam-blog.ear"))
				.as(EnterpriseArchive.class);
	}
}
