Seam Booking Example
====================
This example demonstrates the use of Seam in a Java EE 5 environment.
Transaction and persistence context management is handled by the
EJB container.


How to Build and Deploy the Example
----------------------------------

1. Download and install JBoss AS 4.0.3+

2. Download the SEAM source code distribution from
ttp://www.jboss.com/products/list/downloads#seam

3. Edit the "build.properties" file in the source code bundle
and change jboss.home to your JBoss AS installation directory

4. Build SEAM by running "ant" the source code bundle root directory

5. Build and deploy the example by running "ant" in the
"examples/booking" directory in the source code bundle

6. Start the JBoss AS

7. Access the application at "http://localhost:8080/seam-booking/"

NOTE: The default build uses the HSQL database embedded in JBoss AS.