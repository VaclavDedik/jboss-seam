Seam Tasks Example
==================

This example demonstrates the integration with RestEasy. It runs as an EAR.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the tasks-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-tasks
