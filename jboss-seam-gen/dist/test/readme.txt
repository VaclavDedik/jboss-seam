If you want to run tests using the Eclipse Junit plugin, you'll need to add
all jars from lib/test/ directory to your classpath on the top of your project classpath.
 Using the Run Dialog, select the test class to run, and select these entries from the project tree:

lib/test/*.jar
lib/*.jar

Seam uses Arquillian in its integration testing and uses prepared result from test-build/ subdirectory when you
run ant target:
$ ant test 

