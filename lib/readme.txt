This directory contains all jars needed to compile Seam
and run the unit tests.

The only jars required to run Seam in a full Java EE 5 
environment are:

  jbpm-3.1.jar
  thirdparty-all.jar

To use Seam with Hibernate3 in a J2EE environment, the 
following extra jars are minimally required: 

Hibernate3:
  hibernate-all.jar
  thirdparty-all.jar

MyFaces:
  myfaces-impl.jar
  myfaces-api.jar
  commons-digester-1.6.jar
  commons-codec-1.2.jar
  commons-beanutils.jar

(Or use your preferred JSF 1.1/1.2 implementation.)

To use Seam in with JBoss Embeddable EJB 3.0 or
JBoss Microcontainer, see embedded-ejb/readme.txt
and microcontainer/readme.txt respectively.
