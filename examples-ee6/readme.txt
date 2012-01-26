Seam Example EE6 Applications
=========================
This directory contains the Seam example applications, which have all been
tested on the latest release of JBoss AS 7.1. Consult the readme.txt file in each of 
the examples to see details.

Below is a list of examples with a brief description. The name of the example,
refered to later as ${example.name}, is equivalent to the name of the folder
unless the folder name begins with seam, in which case the prefix "seam" is
omitted (i.e. seamspace -> space).

----------------------------------------------------------------------

booking/              The Seam Booking demo application for EJB 3.0.



Deploying and Testing an Example Application
============================================

These are general instructions for deploying Seam examples. Take a look at the 
readme.txt in the example to see if there are any specific instructions.

How to Build and Deploy an Example on JBoss AS
----------------------------------------------

1. Download and unzip JBoss AS 7.1 from:
   
   http://jboss.org/jbossas/downloads

2. Make sure you have an up to date version of Seam: 

   http://seamframework.org/Download

3. Build the example by running the following command from the Seam
   "examples-ee6/${example.name}" directory:
   
   mvn clean package
   
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

   The WAR examples are ... TODO later

NOTE: The examples use the H2 database embedded in JBoss AS

   
Running The TestNG Tests
------------------------

TestNG tests are executed during building of the application using:

   mvn clean package -P<maven_profile>

TODO: Running the TestNG Tests in Eclipse


Running functional tests on an example
=======================================

The following steps describe executing of functional tests in general. If particular example
does not contain certain profile, it is simply ignored during the maven call.

* Start JBoss AS 4, 5, 6 or Tomcat
* Set JBOSS_HOME or CATALINA_HOME environment property, respectively

To run functional tests on JBoss AS 4.2:

*   mvn clean verify -Pjbossas42,ftest-jbossas

To run functional tests on JBoss AS 5.1:

*   mvn clean verify -Pjbossas51,ftest-jbossas
    
To run functional tests on JBoss AS 6:

*   mvn clean verify -Pjbossas6,ftest-jbossas
    
To run functional tests on Tomcat with Embedded JBoss:

*   mvn clean verify -Ptomcat,ftest-tomcat


