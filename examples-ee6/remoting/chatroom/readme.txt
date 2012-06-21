Seam Chatroom Example
=====================

This example shows using Seam Remoting to subscribe and publish messages to JMS. 

To deploy the example, follow these steps:

* In the example root directory run:

    mvn clean install

* Set JBOSS_HOME environment property.

* In the chatroom-ear directory run:

    mvn jboss-as:deploy

* Open this URL in a web browser: http://localhost:8080/seam-chatroom

To run functional tests for the example, run:

    mvn verify -Pftest-jbossas     

