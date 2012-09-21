package org.jboss.seam.example.booking.test;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.seam.example.booking.Booking;

import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Deployments {
	public static EnterpriseArchive bookingDeployment() {
		return ShrinkWrap.create(ZipImporter.class, "seam-metawidget-booking.ear").importFrom(new File("../booking-ear/target/seam-metawidget-booking.ear"))
				.as(EnterpriseArchive.class);
	}
}
