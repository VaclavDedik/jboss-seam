Seam Hibernate Portlet Example
=======================
This is a port of the Booking example to Hibernate3 with JavaBeans
for the actions. This example is preconfigured to work as a portlet in JBoss Portal 2.4+

How to Build and Deploy the Example on JBoss AS
-----------------------------------------------

1. Download and install JBoss AS 4.0.4+

2. Download the JBoss Seam distribution from:

   http://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/portal" directory

6. Deploy JBoss Portal as per the documentation:
http://docs.jboss.com/jbportal/v2.2/user-guide/en/html/installation.html

7. You must move some libraries and services that Portal depends on in to the /default/deploy directory now:
   a. For this, you will need to download the JBoss AS 4.0.4+ ZIP distribution.
   b. Unpack it in some temporary location.
   c. From JBOSS_HOME/server/default/deploy, copy:
      jboss-bean.deployer, bsh-deployer.xml, mail-service.xml
   d. From JBOSS_HOME/server/default/lib, copy:
      bsh-1.3.jar, bsh-deployer.jar, mail.jar, activation.jar

*** Copy these files to their corresponding destinations in the AS version you installed for Seam***

8. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

9. Point your web browser to:

   http://localhost:8080/portal

Known Issues
-----------------------------------------------
* Works with JBoss Portal 2.4+, Do not attempt to use 2.2 Branch!
* Hot deployment and redeployment of the portal will result in a JDBC error. You can ignore it, as it is being addressed.