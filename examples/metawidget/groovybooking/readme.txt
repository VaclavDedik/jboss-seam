Seam Metawidget GroovyBooking Example
=====================================

This is the Hotel Booking example implemented in Groovy Beans, Metawidget 
and Hibernate JPA. This application runs on JBoss AS, but is deployed as 
an *exploded* WAR rather than an EAR.

A majority of source files in this project comes from non-metawidget GroovyBooking example
in the distribution. Metawidget resource files are placed under src/metawidget 
subdirectory of a booking-web submodule.

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the groovybooking-web directory run:

    mvn jboss-as:deploy

For further Metawidget documentation see http://metawidget.org/documentation.html
