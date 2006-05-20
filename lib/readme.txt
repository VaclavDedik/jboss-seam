This directory contains all jars needed to compile Seam
and run the unit tests.

The only jars required to run Seam in a full Java EE 5 
environment are:

  jbpm-3.1.1.jar
  thirdparty-all.jar

To use Seam with Hibernate3 in a J2EE 1.4 environment, 
the following extra jars are minimally required: 

Hibernate3:
  hibernate-all.jar

MyFaces:
  myfaces-impl-1.1.3.jar
  myfaces-api-1.1.3.jar
  commons-digester-1.6.jar
  commons-codec-1.3.jar
  commons-beanutils-1.7.0.jar
  jstl.jar

(Or use your preferred JSF 1.1/1.2 implementation.)

To use Seam in with JBoss Embeddable EJB 3.0 or JBoss 
Microcontainer, the following extra jars are required:

  jboss-ejb3-all.jar
  hibernate-all.jar

You will also need a JBoss Microcontainer configuration:

JBoss Microcontainer with JTA/JCA:
  microcontainer/conf
  
JBoss Embeddable EJB 3.0:
  embedded-ejb/conf
