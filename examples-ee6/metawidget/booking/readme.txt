Seam Metawidget Booking Example
===============================

This example demonstrates the use of Seam in a Java EE 5 environment.
Transaction and persistence context management is handled by the
EJB container. This example runs on JBoss AS as an EAR or Tomcat 
with JBoss Embedded as a WAR.

A majority of source files in this project comes from non-metawidget Booking example
in the distribution. Metawidget source files are placed under src/metawidget 
subdirectories in booking-ejb and booking-web submodules.

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the booking-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-metawidget-booking

