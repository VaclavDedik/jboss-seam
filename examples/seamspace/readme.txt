Seam Seamspace Example
======================

This example demonstrates Seam Security.

To deploy the example to JBoss AS, follow these steps:

* In the example root directory, run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the seamspace-ear directory, run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-seamspace

Or to deploy it using Ant:

* In the example root directory run:

    ant clean package

* Copy seam-booking.ear from directory booking-ear/target to the 
  deployment directory of JBossAS ($JBOSS_HOME/standalone/deployments 
  by default)

* Open this URL in a web browser: http://localhost:8080/seam-booking
