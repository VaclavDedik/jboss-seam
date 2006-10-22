Seam DVD Store Example
======================
This example demonstrates the use of Seam with jBPM pageflow and
business process management.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.5, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://labs.jboss.com/portal/jbossseam/download/index.html

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/dvdstore" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-dvd/

   NOTE: The default build uses the HSQL database embedded in JBoss AS

How to Build and Deploy the Example on Tomcat
-----------------------------------------------

1. Download and install Tomcat

   NOTE: Due to a bug, you must install Tomcat to a directory
   path with no spaces. The example does not work in a default
   install of Tomcat.
   
2. Download the JBoss Seam distribution from:

   http://labs.jboss.com/portal/jbossseam/download/index.html

3. Edit the "build.properties" file and change tomcat.home to your 
   Tomcat installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant deploy.tomcat" 
   in the Seam "examples/dvdstore" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/jboss-seam-dvd/

