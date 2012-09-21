Seam NestedBooking Example
==========================

This example demonstrates the use of Seam nested conversations and runs 
on JBoss AS as an EAR.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the nestedbooking-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-nestedbooking

