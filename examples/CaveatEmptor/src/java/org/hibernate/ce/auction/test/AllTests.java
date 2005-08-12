package org.hibernate.ce.auction.test;

import junit.framework.*;
import junit.textui.TestRunner;

/**
 * Run all unit tests for CaveatEmptor.
 *
 * @author Christian Bauer <christian@hibernate.org>
 */
public class AllTests {

	public static Test suite() {

		TestSuite suite = new TestSuite();

		suite.addTest( UserTest.suite() );
		suite.addTest( ItemTest.suite() );
		suite.addTest( AuditTest.suite() );
		suite.addTest( CategoryItemTest.suite() );

		return suite;
	}

	public static void main(String args[]) {
		TestRunner.run( suite() );
	}
}
