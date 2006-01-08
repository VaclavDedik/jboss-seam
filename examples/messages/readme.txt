Seam Message List Example
=========================
This is a simple example of the use of @DataModel for the Seam tutorial.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.3, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/registration" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-messages/

   NOTE: The default build uses the HSQL database embedded in JBoss AS

Running The TestNG Test
-----------------------

In the "examples/messages" directory, type "ant testexample"

Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the jboss-seam Eclipse project with the following directories
   in your source path:
   
   src/main/
   examples/messages/src/
   examples/messages/resources/
   embedded-ejb/conf/
   
   And all jar files from the following directories in your classpath:
   
   lib/
   embedded-ejb/lib
   
3. Run examples/messages/src/org/jboss/seam/example/messages/test/testng.xml
   using the TestNG plugin.
   