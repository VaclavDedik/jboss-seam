This is not a regular "Seam example", its a standalone seam-gen project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

INSTALLATION
==========================================================================================

- Install JBoss Application Server 4.2 CR2

- Edit build.properties

- Edit src/etc/META-INF/persistence-dev.xml and change the path to your Lucene index directory

- Run the standalone HSQL DB server with 'ant startdb'

- Call 'ant deploy'

- Access http://localhost:8080/wiki/

