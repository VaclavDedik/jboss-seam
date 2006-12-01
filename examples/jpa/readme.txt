This is the Hotel Booking example implemented in Seam POJO and Hibernate JPA.
It can be deployed in JBoss AS 4.x, WebLogic, Glassfish, Tomcat without
the EJB3 container.

JBoss AS 4.0.5 (with or without EJB3):
  * Install JBoss AS 4.0.5 with the default J2EE profile
  * ant jboss
  * Deploy build/jboss-seam-jpa.war
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-jpa/

WebLogic 9.2:
  * Install WebLogic 9.2
  * ant weblogic
  * Start the WebLogic "examples" server
  * Load the admin console http://localhost:7001/console/
  * Deploy build/jboss-seam-jpa.war
  * Access the app at http://localhost:7001/jboss-seam-jpa/

Tomcat (no embedded EJB3):
  * Install Tomcat
  * ant tomcat
  * Deploy build/jboss-seam-jpa.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-jpa/

Glassfish
  * Install Glassfish v1 UR1
  * ant glassfish
  * Start GlassFish
  * Load the admin console http://localhost:4848/
  * Deploy build/jboss-seam-jpa.war as Web App
  * Access the app at http://localhost:8080/jboss-seam-jpa/

NOTES TO GLASSFISH:
  In order for the app to work out of the box with Glassfish, we have
  used the Derby (i.e., Java DB) database in Glassfish. However,
  we strongly recommend you to use a non-Derby data source (e.g., HSQL
  is a much better embeded DB) if possible. The
  WEB/classes/GlassfishDerbyDialect.class is a special hack to get around
  a Derby bug in Glassfish TM. You must use it as your Hibernate dialect
  if you were to use Derby with Glassfish.

