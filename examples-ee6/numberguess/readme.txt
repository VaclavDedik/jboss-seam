Seam Numberguess Example
========================

This is a simple example for the Seam tutorial, demonstrating the use of
jBPM-based page flow. It runs on both JBoss AS as an EAR.

To deploy the example to JBoss AS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the numberguess-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-numberguess

