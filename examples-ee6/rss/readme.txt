Seam RSS Example
================

This example demonstrates the Seam RSS functionality. It runs on JBoss AS
as an EAR. 

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the rss-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-rss

