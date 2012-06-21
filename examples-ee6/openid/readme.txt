Seam OpenId Example
===================

This is a trivial example of presentation how OpenId authentication is integrated with Seam security.

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the openid-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-openid

