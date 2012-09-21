Seam RestBay Example
====================

This example shows Seam/JAX-RS RESTful HTTP webservices integration.
It runs an EAR.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the restbay-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-restbay

Note: this example doesn't have functional tests, because all functionality is verified in integration tests
