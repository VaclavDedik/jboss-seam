Seam Example Applications
=========================
This directory contains the Seam example applications, which have 
all been tested in JBoss 4.2 GA. Most examples have also been
tested on Tomcat, and some have been tested with other application
servers.

registration/         A trivial example for the tutorial

booking/              The Seam Booking demo application for EJB 3.0

hibernate/            The Seam Booking demo ported to Hibernate3

icefaces/             The Seam Booking demo with ICEfaces, instead of 
                      Ajax4JSF

jee5/            	  The Seam Booking demo ported to the Java EE 5 
                      platforms

dvdstore/             The Seam DVD Store demo demonstrating jBPM 
                      support in Seam
                      
contactlist/          The Seam Contact List demo demonstrating use
                      of the Seam application framework
                
blog/                 The Seam blog example, showing how to write
                      RESTful applications using Seam
                
seamspace/            The Seam Spaces demo demonstrating Seam 
                      Security

seampay/              The Seam Payments demo demonstrating the use of
                      asynchronous methods
                      
numberguess/          The Seam number guessing example, demonstrating
                      jBPM pageflow

todo/                 The Seam todo list example demonstrating
                      jBPM business process management
                      
messages/             The Seam message list example demonstrating use 
                      of the @DataModel annotation
                      
mail/                 The Seam mail example demonstrating use of 
                      facelets-based email templating
                      
pdf/                  The Seam PDF example demonstrating use of 
                      facelets-based PDF templating
                      
ui/                   Demonstrates some Seam JSF controls
                      
spring/               Demonstrates Spring framework integration
                      
drools/               A version of the number guessing example that
                      uses Drools with jBPM
                      
portal/               A port of the Seam Hibernate demo to run on
                      JBoss Portal
                
remoting/helloworld/  A trivial example using Ajax

remoting/chatroom/    The Seam Chat Room example, demostrating Seam
                      Remoting
                      
remoting/progressbar/ An example of an Ajax progress bar

hibernate2/            A revised version of the hibernate example (runs 
                       on J2EE)

jpa/                   An example of the use of Hibernate JPA (runs on 
                       J2EE)

wiki/                 A fully featured wiki system based on Seam, please
                      read wiki/README.txt for installation instructions
                      
seamdiscs/            Demonstrates Seam, Trinidad, Ajax4jsf and Richfaces


Deploying and Testing an Example Application
============================================

These are general instructions for deploying Seam examples. Take a look at the 
readme.txt in the example to see if there are any specific instructions.

How to Build and Deploy an Example on JBoss AS
----------------------------------------------

1. Download and unzip JBoss AS 4.2.1.GA from:
   
   http://labs.jboss.com/jbossas/downloads

2. Make sure you have an up to date version of Seam: 

   http://labs.jboss.com/jbossseam/download

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS directory (Seam uses the default profile)

4. (Optional) Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/${example.name}" directory

6. Start JBoss AS by typing "./run.sh" (on Linux/Unix) or "run" (on Windows) 
   in the jboss-4.2.1.GA/bin directory

7. Point your web browser to:

   http://localhost:8080/seam-${example.name}/

NOTE: The examples use the HSQL database embedded in JBoss AS


How to Build and Deploy the Example on Tomcat
---------------------------------------------

1. Download and install Tomcat 6

   NOTE: Due to a bug, you must install Tomcat to a directory
   path with no spaces. The example does not work in a default
   install of Tomcat.
   
2. Make sure you have an up to date version of Seam: 

   http://labs.jboss.com/jbossseam/download

3. Edit the "build.properties" file and change tomcat.home to your 
   Tomcat directory

4. (Optional) Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant deploy.tomcat" 
   in the Seam "examples/${example.name}" directory

6. Start Tomcat

7. Point your web browser to:

   http://localhost:8080/jboss-seam-${example.name}
   
   
Running The TestNG Tests
------------------------

In the "examples/${example.name}" directory, type "ant test"


Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the jboss-seam Eclipse project with the following directories
   in your source path:
   
   src/main/
   examples/${example.name}/src/
   examples/${example.name}/resources/
   bootstrap/
   
   And all jar files from the following directories in your classpath:
   
   lib/
   
3. Locate and run the testng.xml file using the TestNG plugin
