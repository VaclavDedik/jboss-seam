package org.hibernate.ce.auction.persistence;

import org.hibernate.*;
import org.hibernate.cfg.Configuration;
import org.apache.commons.logging.*;

/**
 * A very simple Hibernate helper class that holds the SessionFactory as a singleton.
 * <p>
 * The only job of this helper class is to give your application code easy
 * access to the <tt>SessionFactory</tt>. It initializes the <tt>SessionFactory</tt>
 * when it is loaded (static initializer) and you can easily open new
 * <tt>Session</tt>s. Only really useful for trivial applications.
 *
 * @author christian@hibernate.org
 */
public class HibernateUtilSimple {

	private static Log log = LogFactory.getLog(HibernateUtil.class);

	private static final SessionFactory sessionFactory;

	// Create the initial SessionFactory from the default configuration files
	static {
		try {
			sessionFactory = new Configuration().configure().buildSessionFactory();
		} catch (Throwable ex) {
			// We have to catch Throwable, otherwise we will miss
			// NoClassDefFoundError and other subclasses of Error
			log.error("Building SessionFactory failed.", ex);
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Session getSession()
		throws HibernateException {
		return sessionFactory.openSession();
	}
}
