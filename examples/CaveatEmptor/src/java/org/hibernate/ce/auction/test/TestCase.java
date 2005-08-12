package org.hibernate.ce.auction.test;

import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.ce.auction.persistence.HibernateUtil;


public abstract class TestCase extends junit.framework.TestCase {

	public TestCase(String s) {
		super(s);
	}

	protected void runTest() throws Throwable {
		try {
			System.out.println("Running test...");
			super.runTest();
		} catch (Throwable e) {
			HibernateUtil.rollbackTransaction();
			throw e;
		} finally{
			HibernateUtil.closeSession();
		}
	}

	protected void setUp() throws Exception {
		super.setUp();
		SchemaExport ddlExport = new SchemaExport(HibernateUtil.getConfiguration());
		ddlExport.create(false, true);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		SchemaExport ddlExport = new SchemaExport(HibernateUtil.getConfiguration());
		ddlExport.drop(false, true);
	}

}
