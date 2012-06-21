Seam Todo List Example
======================

This is a simple example for the Seam tutorial, demonstrating the
use of jBPM-based business process management.

To deploy the example to JBoss AS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the tasks-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-todo

