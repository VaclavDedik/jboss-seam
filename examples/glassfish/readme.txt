
0. Build the demo app: ant clean; ant
   0a. The build target is "build/jboss-seam-glassfish.ear"
   0b. The "ant toplink" target builds the EAR file using toplink as the JPA provider. You will need to manually load the database. Not recommended.

1. Download Glassfish

2. Install it: java -Xmx256m -jar glassfish-installer-xxx.jar

3. Setup glassfish: cd glassfish; ant setup;

4. Start the Glassfish server: bin/asadmin start-domain domain1

5. Start the embedded JavaDB: bin/asadmin start-database

6. Load the admin console: http://localhost:4848/

7. Login using username/password: admin / adminadmin

8. Deploy the "enterprise application" in the admin console: The app is located at SEAM_HOME/examples/glassfish/build/jboss-seam-glassfish.ear

9. Checout the app at: http://localhost:8080/jboss-seam-glassfish/

10. Use the admin console to undeploy the app and stop server
