This is not a regular "Seam example", its a standalone project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

RUNNING THE UNIT TESTS
==========================================================================================

- Call 'ant test'

- Verify the rest results in build/test-output/


INSTALLATION WITH MYSQL (development profile)
==========================================================================================

- Install JBoss Application Server 4.2.2 GA

- Edit build.properties

- Upgrade/downgrade the Hibernate libraries to the ones bundled with this application:
  'ant upgradehibernate' will replace the libraries in server/default/lib of JBoss AS 4.2.2
  and also copy the required ehcache.jar.
  (Sorry, but no other version works currently and the 4.2.2 bundled libraries are too old.)

- Install MySQL 5.x and start it

- Obtain the correct JDBC driver for your MySQL version and copy it into server/default/lib/ of JBoss AS

- The 'dev' default deployment profile will use the default MySQL 'test' database with user 'test' and
  no password, a fresh database schema will be exported to the database on each redeploy

- Call 'ant deploy'

- Start (if you haven't done so already) JBoss AS and access http://localhost:8080/wiki/ and use the
  default login 'admin' with password 'admin'


INSTALLATION WITH MYSQL (production profile)
==========================================================================================

- Install JBoss Application Server 4.2.2 GA

- Upgrade/downgrade the Hibernate libraries bundled with JBoss AS to the libraries bundled
  with this application. Follow the steps outlined above (edit build.properties, call
  'ant upgradehibernate') or copy them manually.

- Install MySQL 5.x

- Obtain the correct JDBC driver for your MySQL version and copy it into server/default/lib/ of JBoss AS

- Call 'ant -Dprofile=prod dist'

- Deploy the SQL schema generated in dist/wiki-ddl.sql on your MySQL database

- Customize the SQL default data in dist/wiki-data.sql before you apply it (especially the baseUrl setting)

- Deploy the SQL data generated in dist/wiki-data.sql on your MySQL database

- Copy the file dist/wiki-ds.xml to server/default/deploy/ directory and edit your MySQL connection settings

- Deploy the dist/wiki.war file by copying it into the server/default/deploy/ directory

- Start (if you haven't done so already) JBoss AS and access http://localhost:8080/wiki (or the
  baseUrl you specified on data import)

- Login as admin/admin and update the Lucene index in the 'Administration' screen

NOTE: The Lucene index directory for full-text searching is named "lacewikiIndex" and located in the
current directory. This is the current directory from which you started the application server! If you want
to change this setting, unpack the WAR and change the META-INF/persistence.xml configuration file.
