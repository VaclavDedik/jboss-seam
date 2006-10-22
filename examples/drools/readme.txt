Seam Drools Example
===================
This is a simple example for the Seam tutorial, demonstrating the
use of jBPM with Drools.

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.5, with the EJB 3.0 profile

2. Download the JBoss Seam distribution from:

   http://labs.jboss.com/portal/jbossseam/download/index.html

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/drools" directory

6. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

7. Point your web browser to:

   http://localhost:8080/seam-drools/
 