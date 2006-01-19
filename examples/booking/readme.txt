Seam Booking Example
====================
This example demonstrates the use of Seam in a Java EE 5 environment.
Transaction and persistence context management is handled by the
EJB container.

This example can also run in Tomcat with the JBoss Embeddable EJB3
container.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.3, with the EJB 3.0 profile

   NOTE: Due to a bug, the demo database content is not loaded 
   properly in AS 4.0.3SP1. If you use 4.0.3SP1, you should
   populate the database by running "resources/import.sql"

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/booking" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-booking/

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

5. Build and deploy the example by running "ant -f build.tomcat.xml" 
   in the Seam "examples/booking" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/jboss-seam-booking/

Running The TestNG Tests
------------------------

In the "examples/booking" directory, type "ant testexample"

Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the jboss-seam Eclipse project with the following directories
   in your source path:
   
   src/main/
   examples/booking/src/
   examples/booking/resources/
   embedded-ejb/conf/
   
   And all jar files from the following directories in your classpath:
   
   lib/
   embedded-ejb/lib
   
3. Run examples/noejb/src/org/jboss/seam/example/booking/test/testng.xml
   using the TestNG plugin.
