This is not a regular "Seam example", its a standalone seam-gen project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

INSTALLATION
==========================================================================================

- Install JBoss AS 4.2 CR2

- Upgrade the Hibernate JARs in server/default/lib/ with the bundled Hibernate JARs

- Upgrade jboss-archive-browsing.jar and ejb3-persistence.jar in server/default/lib

- Add hibernate-commons-annotations.jar, hibernate-search.jar, and lucene-core.jar to server/default/lib/

- Edit src/etc/META-INF/persistence-dev.xml and change the path to your Lucene index directory

- Run the standalone HSQL DB server with 'ant startdb'

- Edit build.properties

- Call 'ant deploy'
