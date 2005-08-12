Hibernate Application: CaveatEmptor (HiA Second Edition)
=========================================================
version 0.9.0, xx. xxx 2004


Purpose
-----------------------------------------------------

CaveatEmptor is an online auction system, it has
entities such as item, user, and bid. This Hibernate
demo application was originally written for the book 
"Hibernate in Action", published by Manning Inc.

This software is distributed under the terms of the FSF
Lesser Gnu Public License (see LGPL.txt).

CaveatEmptor is distributed in several packages, each
having a different focus. This is the "HiA" version,
which only implements the persistence layer of the book
"Hibernate in Action", Second Edition.


Install & Run
-----------------------------------------------------

To install just execute the build script with and, then copy the caveatemptor.ear file to the deploy directory of JBoss.
Point your browser to http://localhost:8080/caveatemptor and follow the instructions.

If you need to clean the database, remove the files
$JBOSS_HOME/bin/caveatemptor.* and $JBOSS_HOME/bin/jbpm.*

You can regenerate the tables by going to http://localhost:8080/caveatemptor/init