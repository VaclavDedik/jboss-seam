package org.hibernate.ce.auction.test;

import junit.framework.*;
import junit.textui.TestRunner;

public class OnlyInitDataTest extends TestCaseWithData {

	// ********************************************************** //

	public void testImportData() throws Exception {
		initData();
	}

	// ********************************************************** //

	protected void tearDown() throws Exception {
		// Don't drop schema in tearDown()...
	}

	// ********************************************************** //

	public OnlyInitDataTest(String x) {
		super(x);
	}

	public static Test suite() {
		return new TestSuite(OnlyInitDataTest.class);
	}

	public static void main(String[] args) throws Exception {
		TestRunner.run( suite() );
	}

}
