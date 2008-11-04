#
 # JBoss, Home of Professional Open Source
 # Copyright 2008, Red Hat Middleware LLC, and individual contributors
 # by the @authors tag. See the copyright.txt in the distribution for a
 # full listing of individual contributors.
 #
 # This is free software; you can redistribute it and/or modify it
 # under the terms of the GNU Lesser General Public License as
 # published by the Free Software Foundation; either version 2.1 of
 # the License, or (at your option) any later version.
 #
 # This software is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 # Lesser General Public License for more details.
 #
 # You should have received a copy of the GNU Lesser General Public
 # License along with this software; if not, write to the Free
 # Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 # 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 # 
 
Core functional test framework for Seam.

UNDER DEVELOPMENT NOT COMPLETE

How To:
----------
* Follow specific instructions for your OS
* Set jboss.home in $SEAM_HOME/build.properties
* Change to the $SEAM_HOME/src/test/ftest directory
  * execute "ant testall" to run all of the functional tests
  * execute "ant test -Dtest=booking" to only run the functional tests for the booking example

Known Limitations:
---------------------
* Only tested on Linux with Firefox
* jboss.home must be set in $SEAM_HOME/build.properties
* JBoss AS must be started (preferably with nothing deployed)

Windows Setup
--------------
TBD

Unix/Linux Setup
-----------------
* You must set to the location of your firefox browser like this:
   export LD_LIBRARY_PATH=/jboss/projects/seam/automation/firefox:$LD_LIBRARY_PATH
   export PATH=/jboss/projects/seam/automation/firefox:$PATH
Otherwise, you get the message:
   Error: com.thoughtworks.selenium.SeleniumException: ERROR Server Exception: sessionId should not be null; has this session been started yet?

Mac OS Setup
--------------
TBD

TODO's:
-------
* There are several TODO's in the source code
* Write up detailed instructions for adding tests, containers, etc...
* Add the ability to download,extract,started, stop, remove containers (jboss 4.2.X, JBoss 5, tomcat 6)
* Consolidate the test reports
* Expose more options to users for tweaking
* headless env and selenium RC integration for CI 
* Test and update for more Browsers, Operating Systems, and Containers
* set up a project file for these tests, or update the existing?
* Describe how to debug the tests using eclipse
  - start server, sel server, and in eclipse set props in ftest.prop file and testng plugin
* We currently have 3 required jars in the $SEAM_HOME/src/test/ftest/lib directory
  - When the build system is updated these will be removed and dependencies will
    be handled as the rest of the source is.
  - We are trying to keep the ftest builds as separate from seam builds as possible.
  - FYI selenium versions are "1.0-beta-1" and testng.jar is 5.8-200803291025

OPEN QUESTIONS:
-----------------
* I'm not sure I like the package name for the common example test code
* I don't like how we are using property files for all the variables
  - Jozef  - can we move them to the values in an interface, or some other way 
