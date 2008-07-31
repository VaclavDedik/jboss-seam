Seam Example Applications
=========================
This directory contains the Seam example applications, which have 
all been tested in the latest release of JBoss 4.2. Most examples have also been
tested on Tomcat (running JDK 1.5), and some have been tested with other 
application servers.



blog/                 The Seam blog example, showing how to write
                      RESTful applications using Seam

booking/              The Seam Booking demo application for EJB 3.0

contactlist/          The Seam Contact List demo demonstrating use
                      of the Seam application framework
                      
drools/               A version of the number guessing example that
                      uses Drools with jBPM

dvdstore/             The Seam DVD Store demo demonstrating jBPM 
                      support in Seam

excel/                Demo of excel export support.
                      
groovybooking/        The Seam Booking demo ported to Groovy

hibernate/            The Seam Booking demo ported to Hibernate3

icefaces/             The Seam Booking demo with ICEfaces, instead of 
                      Ajax4JSF

itext/                A demo of the Seam iText integration for generating pdfs
                      
jee5/booking          The Seam Booking demo ported to the Java EE 5 
                      platforms
                      
jee5/remoting         The Seam remoting helloworld demo ported to the Java EE 5 
                      platforms                      

jpa/                  An example of the use of JPA (provided by Hibernate), runs
                      on many platforms, including non-EE 5 platforms (including
                      plain Tomcat)
                      
mail/                 The Seam mail example demonstrating use of 
                      facelets-based email templating

messages/             The Seam message list example demonstrating use 
                      of the @DataModel annotation
                      
nestedbooking/        The booking example modified to show the use of nested
                      conversations                      

numberguess/          The Seam number guessing example, demonstrating
                      jBPM pageflow

quartz/               A port of the Seampay example to use the Quartz dispatcher

registration/         A trivial example for the tutorial                

remoting/chatroom/    The Seam Chat Room example, demostrating Seam
                      Remoting

remoting/gwt/         An example of using GWT with Seam remoting

remoting/helloworld/  A trivial example using Ajax
                      
remoting/progressbar/ An example of an Ajax progress bar

seambay/              An example of using Seam with Web Services

seamdiscs/            Demonstrates Seam, Trinidad, Ajax4jsf and Richfaces

seampay/              The Seam Payments demo demonstrating the use of
                      asynchronous methods
                
seamspace/            The Seam Spaces demo demonstrating Seam 
                      Security

spring/               Demonstrates Spring framework integration

todo/                 The Seam todo list example demonstrating
                      jBPM business process management
                      
ui/                   Demonstrates some Seam JSF controls

wiki/                 A fully featured wiki system based on Seam, please
                      read wiki/README.txt for installation instructions
                      


Deploying and Testing an Example Application
============================================

These are general instructions for deploying Seam examples. Take a look at the 
readme.txt in the example to see if there are any specific instructions.

How to Build and Deploy an Example on JBoss AS
----------------------------------------------

1. Download and unzip JBoss AS 4.2.2.GA from:
   
   http://labs.jboss.com/jbossas/downloads

2. Make sure you have an up to date version of Seam: 

   http://seamframework.org/Download

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS directory (Seam uses the default profile)

4. (Optional) Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/${example.name}" directory

6. Start JBoss AS by typing "./run.sh" (on Linux/Unix) or "run" (on Windows) 
   in the jboss-4.2.2.GA/bin directory

7. Point your web browser to:

   http://localhost:8080/seam-${example.name}/

NOTE: The examples use the HSQL database embedded in JBoss AS


How to Build and Deploy the Example on Tomcat
---------------------------------------------

1. Download and install Tomcat 6

   NOTE: Due to a bug, you must install Tomcat to a directory
   path with no spaces. The example does not work in a default
   install of Tomcat.
   
2. Install Embedded JBoss as described in the "Configuration" chapter of the
   Seam reerence documentation. 
   
3. Make sure you have an up to date version of Seam: 

   http://seamframework.org/Download

4. Edit the "build.properties" file and change tomcat.home to your 
   Tomcat directory

5. (Optional) Build Seam by running "ant" the Seam root directory

6. Build and deploy the example by running "ant tomcat.deploy" 
   in the Seam "examples/${example.name}" directory

7. Start Tomcat

8. Point your web browser to:

   http://localhost:8080/jboss-seam-${example.name}
   
   
Running The TestNG Tests
------------------------

In the "examples/${example.name}" directory, type "ant test"


Running the TestNG Tests in Eclipse
-----------------------------------

1. Install the TestNG Eclipse plugin from http://beust.com/eclipse

2. Create the TestNG runner with the following directories added to the
   classpath:
   
   examples/${example.name}/src/
   examples/${example.name}/resources/
   bootstrap/
   
   And all jar files from the following directories in your classpath:
   
   lib/test
   
   Make sure all these come before the referenced libraries
   
3. Locate and run the testng.xml file using the TestNG plugin
