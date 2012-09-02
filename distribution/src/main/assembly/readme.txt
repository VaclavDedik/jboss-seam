
JBoss Seam - Contextual Component framework for Java EE
=========================================================
version 2.3.0.CR1, September 2012

This software is distributed under the terms of the FSF Lesser Gnu
Public License (see lgpl.txt). 

Get Up And Running Quick
------------------------
   
1. Install JBoss AS 7.1.1.Final  

2. Start JBoss AS by typing "bin/standalone.sh" in the JBoss home directory

3. In the "examples-ee6/booking" directory, type "mvn clean package" and check 
   for any error messages.

4. In the booking-ear directory run:

    mvn jboss-as:deploy
   
5. Point your browser to    

   http://localhost:8080/seam-booking/
      
6. Register an account, search for hotels, book a room...

Learn more
----------

* Read the documentation in the "doc/reference/en-US" directory
* Read the online FAQ http://www.seamframework.org/Seam2

Notes for this release
----------------------

Be warned that JBoss Embedded is LEGACY runtime and is not in Seam 2.3 distribution anymore
