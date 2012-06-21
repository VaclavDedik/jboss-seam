Seam IceFaces Example
=====================

This example demonstrates integration with IceFaces. It runs on JBoss AS as an
EAR.

To deploy the example to JBoss AS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the icefaces-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-icefaces

To run functional tests for the example on JBoss AS, run:

    mvn verify -Pftest-jbossas
