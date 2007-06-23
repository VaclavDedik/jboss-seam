
0. Build the demo app: ant clean; ant glassfish
   0a. The build target is "build/jboss-seam-jee5.ear"
   0b. The "ant glassfish-toplink" target builds the EAR file using TopLink as the JPA provider. 
       You will need to manually load the database. Not recommended.

1. Download Glassfish v2 -b52 or above (b52 is one of the promoted builds which have based basic QA)

2. Install it: java -Xmx256m -jar glassfish-installer-xxx.jar

3. Setup glassfish: cd glassfish; ant -f setup.xml;

4. Start the Glassfish server: bin/asadmin start-domain domain1

5. Start the embedded JavaDB: bin/asadmin start-database

6. Load the admin console: http://localhost:4848/

7. Login using username/password: admin / adminadmin

8. Deploy the "enterprise application" in the admin console: 
   The app is located at SEAM_HOME/examples/jee5/build/jboss-seam-jee5.ear

9. Checkout the app at: http://localhost:8080/seam-jee5/

10. Stop the server and database: bin/asadmin stop-domain domain1; bin/asadmin stop-database
