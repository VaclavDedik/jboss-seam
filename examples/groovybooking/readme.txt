This is the Hotel Booking example implemented in Groovy Beans and Hibernate JPA..

JBoss AS 4.2 (with or without EJB3):
  * Install JBoss AS 4.2 with the default J2EE profile
  * Set jboss.home in build.properties
  * ant explode
  * Start JBoss AS 
  * Access the app at http://localhost:8080/groovybooking/

When editing Groovy files from action, a simple ant explode is enough
When editing Groovy files from model, ant explode restart is necessary

