Seam Hibernate Portlet Example
==============================
This is a port of the Booking example to Hibernate3 with JavaBeans
for the actions. This example is preconfigured to work as a portlet 
in JBoss Portal 2.4+

How to Build and Deploy the Example on JBoss Portal
---------------------------------------------------

1. Download and install JBoss AS 4.0.4, with the JBoss Portal profile

2. Download the JBoss Seam distribution from:

   http://labs.jboss.com/portal/jbossseam/download/index.html

3. Edit the "build.properties" file and change jboss.home to your 
   JBoss AS installation directory

4. Build Seam by running "ant" the Seam root directory

5. Build and deploy the example by running "ant" in the Seam
   "examples/portal" directory

8. Start JBoss AS by typing "bin/run.sh" in the JBoss home directory

9. Point your web browser to:

   http://localhost:8080/portal

Known Issues
------------

* Hot deployment and redeployment of the portal will result in 
  a JDBC error. You can ignore it, as it is being addressed.
  
* Portlet resize sends the portlet back to the login screen

  