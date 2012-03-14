Seam Excel Example
==================

This example demonstrates the Seam Excel functionality. It runs on JBoss AS7 as
an EAR.

To deploy the example to JBoss AS7 , follow these steps:

* In the example root directory run:

    mvn clean package

* Set JBOSS_HOME environment property.

* Start the AS7 up.

* Deploy the sample:

    $JBOSS_HOME/bin/jboss-cli.sh --connect 'deploy excel-ear/target/seam-excel.ear'

* Open this URL in a web browser: http://localhost:8080/seam-excel
