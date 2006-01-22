Seam Issue Tracker Example
==========================
This example demonstrates Seam's nested conversations and workspace
management.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.4, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Install the MyFaces tomahawk components in JBoss AS by copying
   tomahawk.jar from lib to:
     
   server/default/deploy/jbossweb-tomcat55.sar/jsf-libs
   
   and then editing:
   
   server/default/deploy/jbossweb-tomcat55.sar/conf/web.xml
   
   adding the following <init-param> to the JSP servlet:

   <init-param>
      <description>Tomahawk tlds</description>
      <param-name>tagLibJar1</param-name>
      <param-value>jsf-libs/tomahawk.jar</param-value>
   </init-param> 	

4. Edit the "build.properties" file and change jboss.home to 
   your JBoss AS installation directory

5. Build Seam by running "ant" the Seam root directory

6. Build and deploy the example by running "ant" in the Seam
   "examples/issues" directory

7. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

8. Point your web browser to:

   http://localhost:8080/seam-issues/

   NOTE: The default build uses the HSQL database embedded in JBoss AS
