Seam Metawidget DVD Store Example
=================================

This example demonstrates the use of Seam with jBPM pageflow and business
process management. It uses metawidget to layout the forms. The example runs on
JBoss AS as an EAR and Tomcat with Embedded JBoss as a WAR.

example.name=dvdstore

The source files in this example are just the overrides needed to utilize
metawidget. Before you can deploy the example, you have to merge these
overrides with the original groovybooking source code in a staging directory.
That is done using the following command:

  ant build
