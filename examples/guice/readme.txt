Seam Guice Example
======================
This is a simple example demonstrating the use of Google Guice with Seam.

example.name=guice

To deploy the example to JBossAS, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the guice-ear directory run:

    mvn jboss-as:deploy

