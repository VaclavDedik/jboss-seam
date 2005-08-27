Building and running:

cvs co jboss-head
cd jboss-head/build
./build.sh
export JBOSS_HOME=XXXX/jboss-head/build/output/jboss-5.0.0alpha
cd ../..
cvs co jboss-seam-head
cd build
ant
cd ../examples/booking
ant deploy
cd $JBOSS_HOME
bin/run.sh

Browse to http://localhost:8080/seam-booking/faces/login.jsp
