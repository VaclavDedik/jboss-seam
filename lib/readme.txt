This directory contains all jars needed to compile Seam.

The only jars required to run Seam in a full Java EE 5 
environment are:

  jbpm-3.0.jar
  cglib-2.1.1.jar
  asm.jar
  jboss-common.jar (for JBoss logging only)

To use Seam with Hibernate3 in a J2EE environment, the 
following extra jars are minimally required: 

Hibernate3:
  hibernate3.jar
  hibernate-annotations.jar
  ejb3-persistence.jar

MyFaces:
  myfaces-impl.jar
  myfaces-jsf-api.jar
  myfaces.jar

(Or use your preferred JSF 1.1 implementation.)

To use Seam in with JBoss Embeddable EJB 3.0 or
JBoss Microcontainer, see embedded-ejb/readme.txt
and microcontainer/readme.txt respectively.
