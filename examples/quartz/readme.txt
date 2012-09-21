Seam Quartz Example
===================

This example demonstrates the Seam Quartz Timer functionality.

To deploy the example to JBoss AS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the quartz-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-quartz
