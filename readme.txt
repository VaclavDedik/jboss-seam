JBoss Seam - Contextual Component framework for Java EE 5
=========================================================
version 1.1.0.CR1, November 2006

This software is distributed under the terms of the FSF Lesser Gnu
Public License (see lgpl.txt).

Get Up And Running Quick
------------------------
   
1. Install JBoss AS 4.0.5.GA with the EJB 3.0 profile selected
   using the JEMS installer
   
   http://jboss.sourceforge.net/jnlp/jems-installer-1.2.0.BETA3.jnlp

2. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

3. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

4. In the "examples/booking" directory, type "ant deploy" and check 
   for any error messages
   
5. Point your browser to 

   http://localhost:8080/seam-booking/
   
6. Register an account, search for hotels, book a room...

Running The TestNG Tests
------------------------

In the jboss-seam-1.x directory, type "ant testcore" or "ant testall"

Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the jboss-seam Eclipse project with the following directories
   in your source path:
   
   src/main/
   src/test/misc
   
   And all jar files from the lib/ directory in your classpath.
   
3. Run src/test/misc/org/jboss/seam/test/testng.xml using the TestNG 
   plugin.

Learn more
----------

* Read the documentation in the "doc/reference/en" directory
* Read the online FAQ http://www.jboss.com/products/seam/faq
