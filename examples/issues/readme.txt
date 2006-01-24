Seam Issue Tracker Example
==========================
This example demonstrates Seam's nested conversations and workspace
management.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.4, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file and change jboss.home to 
   your JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/issues" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-issues/

   NOTE: The default build uses the HSQL database embedded in JBoss AS


How to Build and Deploy the Example on Tomcat
-----------------------------------------------

1. Download and install Tomcat

   NOTE: Due to a bug, you must install Tomcat to a directory
   path with no spaces. The example does not work in a default
   install of Tomcat.
   
2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file and change tomcat.home to your 
   Tomcat installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant deploy.tomcat" 
   in the Seam "examples/issues" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/jboss-seam-issues/

   