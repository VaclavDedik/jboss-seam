
0. Build the demo app: ant clean; ant oc4j - the build target is "build/jboss-seam-oc4j.ear"

1. Download OC4J 11g Technology Preview from here 
   http://www.oracle.com/technology/tech/java/oc4j/11/index.html

2. Unzip the downloaded file

3. Make sure you have $JAVA_HOME and $ORACLE_HOME set as environment variables ($ORACLE_HOME is the
   directory to which you unzip OC4J)

   For further information on installing OC4J, consult the Readme.txt distributed with OC4J

4. Edit the OC4J datasource $ORACLE_HOME/j2ee/home/config/data-sources.xml and, inside <data-sources>,
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


5. Edit $ORACLE_HOME/j2ee/home/config/server.xml and, inside <application-server>, add

   <application 
     name="jboss-seam-jee5" 
     path="../../home/applications/jboss-seam-jee5.ear" 
     parent="default" 
     start="true" 
     />

6. Edit $ORACLE_HOME/j2ee/home/config/default-web-site.xml, and, inside <web-site>, add

   <web-app 
     application="jboss-seam-jee5" 
     name="jboss-seam-jee5" 
     load-on-startup="true" 
     root="/seam-jee5" 
     />

7. Copy hsqldb.jar to OC4J: cp ../../seam-gen/lib/hsqldb.jar $ORACLE_HOME/j2ee/home/applib/

8. Copy the application to OC4J: cp build/jboss-seam-jee5.ear $ORACLE_HOME/j2ee/home/applications/

9. Start OC4J: $ORACLE_HOME/bin/oc4j -start
   a. You will be asked to set the admin password if this is the first time you've started OC4J
   b. You may get an ClassNotFoundException relating to org.jboss.logging.util.OnlyOnceErrorHandler,
      this doesn't impact on the running of the app.  We are working to get rid of this error!

10. Checout the app at: http://localhost:8888/seam-jee5

11. You can stop the server by pressing CTRL-c in the console on which the server is running.


Workarounds
------------

* Set hibernate.query.factory_class=org.hibernate.hql.classic.ClassicQueryTranslatorFactory in
  persistence.xml - OC4J uses an incompatible (old) version of antlr in toplink which causes 
  hibernate to throw an exception (discussed here for Weblogic, but the same applies to OC4J - 
  http://hibernate.org/250.html#A23).  You can also work around this by putting the hibernate 
  jars in $ORACLE_HOME/j2ee/home/applib/