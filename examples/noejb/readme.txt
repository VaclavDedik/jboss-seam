Seam Hibernate3 Example
=======================
This is a port of the booking example to Hibernate3 with JavaBeans
for the actions. Seam handles the JTA transaction demarcation and
Hibernate session management. 

This example can run in a J2EE environment, a Java EE 5 environment, 
or in a servlet container with the JBoss Microcontainer for JTA/JCA 
support.

How to Build and Deploy the Example on Tomcat
-----------------------------------------------

1. Download and install Tomcat

   NOTE: Due to a bug, you must install Tomcat to a directory
   path with no spaces. The example does not work in a default
   install of Tomcat.
   
2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file in the source code bundle
   and change tomcat.home to your Tomcat installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant -f build.tomcat.xml" 
   in the Seam "examples/noejb" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/tomcat-seam-noejb/

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.3

   NOTE: Due to a bug, the demo database content is not loaded 
   properly in AS 4.0.3SP1. If you use 4.0.3SP1, you should
   populate the database by running "resources/import.sql"

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file in the source code bundle
   and change jboss.home to your JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/noejb" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/jboss-seam-noejb/
