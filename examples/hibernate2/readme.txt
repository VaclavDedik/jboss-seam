This is the Hotel Booking example implemented in Seam and Hibernate POJOs.
It can be deployed in JBoss AS 4.x, WebLogic, Glassfish, Tomcat without
the EJB3 container.

JBoss AS 4.0.5 (no EJB3):
  * Install JBoss AS 4.0.5 with the default J2EE profile
  * ant jboss
  * Deploy build/jboss-seam-hibernate.war
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

WebLogic 9.2:
  * Install WebLogic 9.2
  * ant weblogic
  * Start WebLogic
  * Load the admin console http://localhost:7001/console/
  * Deploy build/jboss-seam-hibernate.war
  * Access the app at http://localhost:7001/jboss-seam-hibernate/

Tomcat (no embedded EJB3):
  * Install Tomcat
  * ant tomcat
  * Deploy build/jboss-seam-hibernate.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

Glassfish
  * Install Glassfish v1 UR1
  * ant glassfish
  * Start GlassFish
  * Load the admin console http://localhost:4848/
  * Deploy build/jboss-seam-hibernate.war as Web App
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

