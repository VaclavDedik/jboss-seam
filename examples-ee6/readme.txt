Seam Example EE6 Applications
=========================
This directory contains the Seam example applications, which have all been
tested on the latest release of JBoss AS 7.1.1. Consult the readme.txt file in each of 
the examples to see details.

Below is a list of examples with a brief description. The name of the example,
refered to later as ${example.name}, is equivalent to the name of the folder
unless the folder name begins with seam, in which case the prefix "seam" is
omitted (i.e. seamspace -> space).

----------------------------------------------------------------------

booking/              The Seam Booking demo application for EJB 3.



Deploying and Testing an Example Application
============================================

These are general instructions for deploying Seam examples. Take a look at the 
readme.txt in the example to see if there are any specific instructions.

How to Build and Deploy an Example on JBoss AS
----------------------------------------------

1. Download and unzip JBoss AS 7.1.1 from:
   
   http://jboss.org/jbossas/downloads

2. Make sure you have an up to date version of Seam: 

   http://seamframework.org/Download

3. Build the example by running the following command from the Seam
   "examples-ee6/${example.name}" directory:
   
   mvn clean install   

   NOTE: Firstly, this command will also run unit tests on that example. To skip the tests add 
   -Dmaven.test.skip=true to the maven call. Secondly, there is an option to deploy an "exploded"
   archive. For this purpose, use -Pexploded maven profile.

4. Deploy the example by calling g JBOSS_HOME property and running the 
   following command from the Seam "examples-ee6/${example.name}/{example.name}-ear" directory:

   mvn jboss-as:deploy
    
   To undeploy the example, run:

   mvn jboss-as:undeploy

5. Point your web browser to:

   http://localhost:8080/seam-${example.name}

   Recall that ${example.name} is the name of the example folder unless the
   folder begins with seam, in which the prefix "seam" is omitted. The
   context path is set in META-INF/application.xml for EAR deployments.

   However, WAR deployments use a different naming convention for the context
   path. If you deploy a WAR example, point your web browser to:

   http://localhost:8080/jboss-seam-${example.name}

   The WAR examples are:
   spring, jpa, hibernate, groovybooking 

NOTE: The examples use the H2 database embedded in JBoss AS

   
Running The Integration Tests
------------------------

Integration tests are executed during building of the application using:

   mvn clean install -Darquillian=jbossas-{managed,remote}-7


Running integration test(s) in Eclipse
------------------------
Detail guide is at http://docs.jboss.org/arquillian/reference/1.0.0.Alpha1/en-US/html_single/#d0e552


Debugging of integration test(s) in Eclipse
-----------------------
http://docs.jboss.org/arquillian/reference/1.0.0.Alpha1/en-US/html_single/#d0e974


Running functional tests on an example
=======================================

The following steps describe executing of functional tests in general. If particular example
does not contain certain profile, it is simply ignored during the maven call.

* Start JBoss AS 7
* Set JBOSS_HOME environment property, respectively

To run functional tests:

*   mvn clean verify -Pftest-jbossas
