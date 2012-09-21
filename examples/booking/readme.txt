Seam Booking Example
====================

This example demonstrates the use of Seam in a Java EE 6 environment.
Transaction and persistence context management is handled by the
EJB container. This example runs on JBoss AS as an EAR

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* In the booking-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-booking

