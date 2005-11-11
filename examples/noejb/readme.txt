Seam Hibernate3 Example
=======================
This is a port of the booking example to Hibernate3 with JavaBeans
for the actions. Seam handles the JTA transaction demarcation and
Hibernate session management. This code can run in a J2EE
environment, or in a servlet container with the JBoss Microcontainer
for JTA/JCA support.


### TODO: REVISE THE FOLLOWING INSTRUCTIONS ###

To run the example with JBoss Microcontainer on Tomcat:

1. Edit the build.tomcat.xml file and set your tomcat.home location.
2. Run "ant -f build.tomcat.xml deploy"
3. Access the application at http://localhost:8080/jboss-seam-noejb/

To run the standalone test suite (TestNG):

1. Run "ant -f build.tomcat.xml test"

Create your own first JBoss Seam project by copying this directory,
and use it as a skeleton. You will have to copy all required
libraries as well, see the build.tomcat.xml file for instructions.

