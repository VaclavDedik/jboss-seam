Seam Blog Example
=================

This example demonstrates the use of Seam in a Java EE environment.
Transaction and persistence context management is handled by the EJB container.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the blog-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-blog
