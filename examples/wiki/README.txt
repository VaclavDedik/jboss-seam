This is not a regular "Seam example", its a standalone seam-gen project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

INSTALLATION WITH HSQL DB
==========================================================================================

- Install JBoss Application Server 4.2 GA

- Edit build.properties

- Run the standalone HSQL DB server with 'ant startdb'

- Call 'ant deploy'

- Access http://localhost:8080/wiki/

INSTALLATION WITH MYSQL
==========================================================================================

- Install JBoss Application Server 4.2 GA

- Install MySQL 5.x

- Obtain the correct JDBC driver for your MySQL version and copy it into server/default/lib/ of JBoss AS

- Call 'ant -Dprofile=prod dist'

- Deploy the SQL schema generated in the dist/wiki-ddl.sql on your MySQL database

- Deploy the SQL data generated in the dist/wiki-data.sql on your MySQL database

- Copy the file dist/wiki-ds.xml to server/default/deploy/ directory and edit your MySQL connection settings

- Deploy the dist/wiki.war file by copying it into the server/default/deploy/ directory

- Start (if you haven't done so already) JBoss AS and access http://localhost:8080/wiki/


NOTE: The lucene index directory is named "lacewikiIndex" and located in the current directory. This
is the current directory from which you started the application server! If you want to change this setting,
unpack the WAR and change the META-INF/persistence.xml configuration file.

WARNING: Do not use this software in production! The database schema is not final and will change!
No migration scripts for existing data will be provided until the software is production ready.
