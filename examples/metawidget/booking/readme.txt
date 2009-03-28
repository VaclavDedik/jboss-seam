Seam Metawidget Booking Example
===============================

This example demonstrates the use of Seam in a Java EE 5 environment.
Transaction and persistence context management is handled by the EJB container.
Forms are layed out using Metawidget.

The source files in this example are just the overrides needed to utilize
metawidget. Before you can deploy the example, you have to merge these
overrides with the original groovybooking source code in a staging directory.
That is done using the following command:

  ant build

This example runs on JBoss AS as an EAR or Tomcat with JBoss Embedded as a WAR.

 example.name=booking

To deploy this application to a cluster, first follow the steps 1-9 clustering-howto.txt in the root folder of the Seam distribution. Then execute the following command:

 ant farm

This command will deploy the archive to the farm directory of the "all" JBoss AS domain. To undeploy, run the following command:

 ant unfarm

HTTP session replication is enabled by default. You can disable it with the following commandline switch:

 -Dsession.replication=false

You can also toggle Seam's ManagedEntityInterceptor for any deployment with the following commandline switch:

 -Ddistributable=false

