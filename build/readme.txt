Seam Build System
------------------

This readme describes the build and dependency management system used to build 
Seam, it's examples, and seam-gen.  If you are looking for information on 
building or configuring dependencies for a project which uses Seam, you should
look at the chapter on dependencies and building in the reference manual.

Dependency Management
---------------------

The dependency managmement for Seam is managed through Maven pom files.  The
pom's are located in the build/ directory.  The root.pom.xml is the 'root' or
parent pom for Seam, and contains versioning information for all dependencies.
Each Seam jar has it's own pom (e.g. pdf.pom.xml) which declares the dependencies
for that module - it has the root pom declared as it's parent; no version 
information is placed in this pom.

Seam directly uses the 'compile' dependencies to build the various modules,
and the test scope (for core) to run core tests.

To add or upgrade a dependency of Seam:

* Find the dependency in a maven repository - check repository.jboss.org/maven2
  first and then try mvnsearch.com.
* Add or update the entry in root.pom.xml including version information
* If it's a new dependency, add an entry to the correct modul.  If it's an
  optional dependency (most are), mark it <optional>true</optional>.  If it's
  provided by JBoss AS (current targeted version), mark it <scope>provided</scope>
* Bear in mind that a released Seam shouldn't depend on a SNAPSHOT version, so
  it might be better to take a snapshot, and add it to Seam's local repository
  as a custom version
* When we release Seam we have to add all it's dependencies to 
  repository.jboss.org (no thirdparty repositories should be used for released
  versions) - so if you are adding a dependency which is stable, and you aren't
  planning to change the dependency before the next release you should consider
  adding it to repository.jboss.org straight away.  The proceedure for this is
  outlined at http://wiki.jboss.org/wiki/Wiki.jsp?page=MavenThirdPartyJars
  
To add a dependency of Seam to the development repository stored in CVS:

* If you need a dependency which isn't available in Maven, and don't want to add
  it straight to repository.jboss.org or want to depend on a CVS/snapshot of a 
  project which you're planning to upgrade before the next Seam release you can
  add a dependency to Seam's local (development) repository.  These dependencies
  are (for now) just available when building Seam itself (or if a user adds the
  local repository to their pom).
* To add a jar to the local repository, you can, if you have a pom (that you
  copied from an earlier version or have written) run:

  ant deployLocal -Dpom=foo.pom -Djar=foo.jar
  
  If you want maven to create a basic pom for you:
  
  ant deployLocalJar -Djar=foo.jar
  
* If you need to alter the pom or jar in the local repository but don't change
  the version number, you'll need to delete the old copy from maven's cache
  
  rm -rf ~/.m2/repository/group/id/artifactId/version
  

Release Instructions
--------------------

TBD

Examples
--------

The examples assemble all the Seam dependencies into a staging directory (/lib).
/lib/*.jar is used as the classpath to compile the examples, and the examples
use pattern's to select the jars to put in their deployed archives.

Some trickery (excluding jars) is required to get JBoss Embedded to run 
currently - this should be improved.