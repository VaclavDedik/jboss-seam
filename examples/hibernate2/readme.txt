This is the Hotel Booking example implemented in Seam and Hibernate POJOs.
It can be deployed in JBoss AS 4.x, WebLogic, Glassfish, Tomcat without
the EJB3 container.

JBoss AS 4.0.5 (with or without EJB3):
  * Install JBoss AS 4.0.5 with the default J2EE profile
  * ant jboss
  * Deploy dist-jboss/jboss-seam-hibernate.war
  * Start JBoss AS 
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

WebLogic 9.2:
  * Install WebLogic 9.2
  * ant weblogic92
  * Start the WebLogic "examples" server
  * Load the admin console http://localhost:7001/console/
  * Deploy dist-weblogic92/jboss-seam-hibernate.war
  * Access the app at http://localhost:7001/jboss-seam-hibernate/

WebSphere 6.1:

  * Install and run WebSphere 6.1
  * ant websphere61
  * Install dist-websphere61/jboss-seam-hibernate.war and specify a context_root
  * From the "Enterprise Applications" list select: "jboss-seam-hibernate_war" --> "Manager Modules" --> "jboss-seam-hibernate.war" --> "Classes loaded with application class loader first", and then Apply
  * Start the application
  * Access it at http://localhost:9080/context_root/index.html

Plain Tomcat
  * Install Tomcat 5.5
  * Copy the JARs in lib/tomcat into $TOMCAT_HOME/common/lib
  * Copy the following Context element into $TOMCAT_HOME/conf/server.xml
  * ant tomcat
  * Deploy dist-tomcat/jboss-seam-hibernate.war to $TOMCAT_HOME/webapps/jboss-seam-hibernate.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-hibernate/
Example of the Context element in server.xml:
<Server ...>
  <Service ...>
    <Engine ...>
      <Host ...>
        <Context path="/jboss-seam-hibernate" docBase="jboss-seam-hibernate"
                 debug="5" reloadable="true" crossContext="true">

          <Resource name="jdbc/TestDB" auth="Container" 
               type="javax.sql.DataSource"
               maxActive="100" maxIdle="30" maxWait="10000"
               username="sa" driverClassName="org.hsqldb.jdbcDriver"
               url="jdbc:hsqldb:."/>

        </Context>

Tomcat with embeddable JBoss (the build is the same as JBoss 4.2.0 GA WAR):
  * Install Tomcat
  * Install Embeddable JBoss
  * ant jboss
  * Deploy dist-jboss/jboss-seam-hibernate.war
  * Start Tomcat
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

Glassfish
  * Install Glassfish v1 UR1
  * ant glassfish
  * Start GlassFish
  * Load the admin console http://localhost:4848/
  * Deploy dist-glassfish/jboss-seam-hibernate.war as Web App
  * Access the app at http://localhost:8080/jboss-seam-hibernate/

NOTES TO GLASSFISH:
  In order for the app to work out of the box with Glassfish, we have
  used the Derby (i.e., Java DB) database in Glassfish. However,
  we strongly recommend you to use a non-Derby data source (e.g., HSQL
  is a much better embeded DB) if possible. The 
  WEB/classes/GlassfishDerbyDialect.class is a special hack to get around 
  a Derby bug in Glassfish TM. You must use it as your Hibernate dialect
  if you were to use Derby with Glassfish.
