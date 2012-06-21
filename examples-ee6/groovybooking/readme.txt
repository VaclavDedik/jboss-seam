Seam GroovyBooking Example
==========================

This is the Hotel Booking example implemented in Groovy Beans and Hibernate JPA.
The application is deployed as an *exploded* WAR rather than an EAR.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the groovybooking-web directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/jboss-seam-groovybooking
