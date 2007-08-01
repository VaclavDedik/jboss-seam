Seam JEE5 Example
=================

This is the Hotel Booking example for JEE5 certified application servers listed below.

Glassfish v2 
------------

1.  Build the demo app: ant clean; ant glassfish
    * The build target is "build/jboss-seam-jee5.ear"
    * The "ant glassfish-toplink" target builds the EAR file using TopLink as the JPA provider. 
      You will need to manually load the database. Not recommended.

2.  Download Glassfish v2 -b52 or above (b52 is one of the promoted builds which have based basic QA)

3.  Install it: java -Xmx256m -jar glassfish-installer-xxx.jar

4.  Setup glassfish: cd glassfish; ant -f setup.xml;

5.  Start the Glassfish server: bin/asadmin start-domain domain1

6.  Start the embedded JavaDB: bin/asadmin start-database

7.  Load the admin console: http://localhost:4848/

8.  Login using username/password: admin / adminadmin

9.  Deploy the "enterprise application" in the admin console: 
    The app is located at SEAM_HOME/examples/jee5/build/jboss-seam-jee5.ear

10. Checkout the app at: http://localhost:8080/seam-jee5/

11. Stop the server and database: bin/asadmin stop-domain domain1; bin/asadmin stop-database


OC4J 11g Technology Preview
---------------------------

1.  Build the demo app: ant clean; ant oc4j - the build target is "build/jboss-seam-oc4j.ear"

2.  Download OC4J 11g Technology Preview from here 
    http://www.oracle.com/technology/tech/java/oc4j/11/index.html

3.  Unzip the downloaded file

4.  Make sure you have $JAVA_HOME and $ORACLE_HOME set as environment variables ($ORACLE_HOME is the
    directory to which you unzip OC4J)
    * For further information on installing OC4J, consult the Readme.txt distributed with OC4J

5.  Edit the OC4J datasource $ORACLE_HOME/j2ee/home/config/data-sources.xml and, inside <data-sources>,
    add

    <managed-data-source 
      connection-pool-name="jee5-connection-pool" 
      jndi-name="jdbc/__default" 
      name="jee5-managed-data-source"
      />
    <connection-pool name="jee5-connection-pool">
      <connection-factory 
        factory-class="org.hsqldb.jdbcDriver" 
        user="sa" 
        password="" url="jdbc:hsqldb:." 
        />
    </connection-pool>


6.  Edit $ORACLE_HOME/j2ee/home/config/server.xml and, inside <application-server>, add

    <application 
      name="jboss-seam-jee5" 
      path="../../home/applications/jboss-seam-jee5.ear" 
      parent="default" 
      start="true" 
      />

7.  Edit $ORACLE_HOME/j2ee/home/config/default-web-site.xml, and, inside <web-site>, add

    <web-app 
      application="jboss-seam-jee5" 
      name="jboss-seam-jee5" 
      load-on-startup="true" 
      root="/seam-jee5" 
      />

8.  Copy hsqldb.jar to OC4J: cp ../../seam-gen/lib/hsqldb.jar $ORACLE_HOME/j2ee/home/applib/

9.  Copy the application to OC4J: cp build/jboss-seam-jee5.ear $ORACLE_HOME/j2ee/home/applications/

10. Start OC4J: $ORACLE_HOME/bin/oc4j -start
    * You will be asked to set the admin password if this is the first time you've started OC4J
    * You may get an ClassNotFoundException relating to org.jboss.logging.util.OnlyOnceErrorHandler,
      this doesn't impact on the running of the app.  We are working to get rid of this error!

10. Checout the app at: http://localhost:8888/seam-jee5

11. You can stop the server by pressing CTRL-c in the console on which the server is running.


Workarounds for OC4J 11g
------------------------

* Set hibernate.query.factory_class=org.hibernate.hql.classic.ClassicQueryTranslatorFactory in
  persistence.xml - OC4J uses an incompatible (old) version of antlr in toplink which causes 
  hibernate to throw an exception (discussed here for Weblogic, but the same applies to OC4J - 
  http://hibernate.org/250.html#A23).  You can also work around this by putting the hibernate 
  jars in $ORACLE_HOME/j2ee/home/applib/