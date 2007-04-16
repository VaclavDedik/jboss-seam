This shows GWT with Seam remoting.

run ant deploy, and then browse to:

http://localhost:8080/seam-helloworld/org.jboss.seam.example.remoting.gwt.HelloWorld/HelloWorld.html

(or similar).


GWT:
If you want to rebuild the GWT front end, you will need to download GWT, and configure build.properties to point to it.
 - you can then run "ant gwt-compile" from this directory. It is pre-built by default.
If you want to use the GWT hosted mode, well, read all about it from the GWT docs !
