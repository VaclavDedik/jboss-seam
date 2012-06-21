Seam Remoting/Progressbar Example
=================================

This example shows how to create a progress bar using Seam Remoting. 
It runs on JBoss AS as an EAR.

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the progressbar-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-progressbar
