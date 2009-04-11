Seam Metawidget DVD Store Example
=================================

This example demonstrates the use of Seam with jBPM pageflow and business
process management. It uses metawidget to layout the forms. The example runs on
JBoss AS as an EAR and Tomcat with Embedded JBoss as a WAR.

example.name=dvdstore

The source files in this example are just the overrides needed to utilize
metawidget. Before the example is built, these overrides are merged with the
original dvdstore source code in a staging directory. This step is performed
by following command:

  ant stage

The stage command is automatically called on any Ant build, so you can simply run:

  ant explode
