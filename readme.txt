JBoss Seam - Contextual Component framework for Java EE 5
=========================================================
version 2.0.1.GA, January 2008

This software is distributed under the terms of the FSF Lesser Gnu
Public License (see lgpl.txt). 

Get Up And Running Quick
------------------------
   
1. Install JBoss AS 4.2.2.GA.  The JEMS installer is no longer required for EJB3 support.

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

In the jboss-seam-x.x.x.x directory, type "ant testcore" or "ant testall"

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
* Read the online FAQ http://www.seamframework.org
