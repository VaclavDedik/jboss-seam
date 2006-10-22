Seam Todo List Example
======================
This is a simple example for the Seam tutorial, demonstrating the
use of jBPM-based business process management.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.5, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://labs.jboss.com/portal/jbossseam/download/index.html

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/todo" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-todo/

How to Build and Deploy the Example on Tomcat
---------------------------------------------

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
   in the Seam "examples/todo" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/jboss-seam-todo/

Running The TestNG Test
-----------------------

In the "examples/todo" directory, type "ant testexample"

Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the jboss-seam Eclipse project with the following directories
   in your source path:
   
   src/main/
   examples/todo/src/
   examples/todo/resources/
   microcontainer/conf
   
   And all jar files from the following directories in your classpath:
   
   lib/
   
3. Run examples/todo/src/org/jboss/seam/example/todo/test/testng.xml
   using the TestNG plugin.
   