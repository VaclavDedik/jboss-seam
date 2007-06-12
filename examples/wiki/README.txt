This is not a regular "Seam example", its a standalone seam-gen project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

INSTALLATION
==========================================================================================

- Install JBoss Application Server 4.2 CR2

- Edit build.properties

- Run the standalone HSQL DB server with 'ant startdb'

- Call 'ant deploy'

- Access http://localhost:8080/wiki/

NOTE: The lucene index directory is named "lacewikiIndex" and located in the current directory. This
is the current directory from which you started the application server! If you want to change this setting,
unpack the WAR and change the META-INF/persistence.xml configuration file.

WARNING: Do not use this software in production! The database schema is not final and will change!
No migration scripts for existing data will be provided until the software is production ready.
