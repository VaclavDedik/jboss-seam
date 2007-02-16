This is not a regular "Seam example", its a standalone seam-gen project structure. The build.xml
references the existing Seam libraries and thirdparty libs, however, to avoid duplication.

This is configured for standalone HSQL DB on localhost (I like being able to acces
the database with a SQL client). So start it with 'java -cp hsqldb.jar org.hsqldb.Server'
before deploying.

The start document is hardcoded in index.html to be one of the records from import.sql.
