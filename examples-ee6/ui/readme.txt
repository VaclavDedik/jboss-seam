Seam UI Example
===============

This is a simple example demonstrating Seam UI. It runs on JBoss
AS7 as an EAR

To deploy the example to JBoss AS7, follow these steps:

* In the example root directory run:

    mvn clean package

* Set JBOSS_HOME environment property.

* Start the AS7 up.

* In the ui-ear directory run the following to create a ui datasource:

    $JBOSS_HOME/bin/jboss-cli.sh --file=ui-ds.cli

* Deploy the sample:

    $JBOSS_HOME/bin/jboss-cli.sh --connect 'deploy ui-ear/target/seam-ui.ear'

* Open this URL in a web browser: http://localhost:8080/seam-ui
