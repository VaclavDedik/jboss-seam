#Name of your project - cannot be "jboss-seam"
project.name=@projectName@

#Location for the project's files
workspace.home=${projectProps.workspaceHome}

#JBoss Installation Home - Must be an ejb3 configuration
jboss.home=${projectProps.jbossHome}

#Java package for action objects (SFSB's, SLSBs)
action.dir=${projectProps.actionDir}
action.package=${projectProps.actionPackage}

#Java package for model objects (Entity beans)
model.dir=${projectProps.modelDir}
model.package=${projectProps.modelPackage}

#Java package for test cases (TestNG)
test.dir=${projectProps.testDir}
test.package=${projectProps.testPackage}

#Java package for messaging objects (MDBs)
mdb.dir=${projectProps.mdbDir}
mdb.package=${projectProps.mdbPackage}

eclipse.wtp=${projectProps.wtp}
