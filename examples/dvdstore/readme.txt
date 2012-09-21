Seam DVD Store Example
======================

This example demonstrates the use of Seam with jBPM pageflow and business
process management. It runs on JBoss AS as an EAR and Tomcat with Embedded
JBoss as a WAR.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the dvdstore-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-dvdstore

