Seam DVD Store Example
======================
This example demonstrates the use of Seam with jBPM pageflow and
business process management.

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
   "examples/dvdstore" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/dvd/

   NOTE: The default build uses the HSQL database embedded in JBoss AS

