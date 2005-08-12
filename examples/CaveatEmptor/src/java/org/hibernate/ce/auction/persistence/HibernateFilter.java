package org.hibernate.ce.auction.persistence;

import org.apache.commons.logging.*;

import javax.servlet.*;
import java.io.IOException;

/**
 * A servlet filter that opens and closes a Hibernate Session for each request.
 * <p>
 * This filter guarantees a sane state, committing any pending database
 * transaction once all other filters (and servlets) have executed. It also
 * guarantees that the Hibernate <tt>Session</tt> of the current thread will
 * be closed before the response is send to the client.
 * <p>
 * Use this filter for the <b>session-per-request</b> pattern and if you are
 * using <i>Detached Objects</i>.
 *
 * @see HibernateUtil
 * @author Christian Bauer <christian@hibernate.org>
 */
public class HibernateFilter implements Filter {

	private static Log log = LogFactory.getLog(HibernateFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Servlet filter init, now opening/closing a Session for each request.");
	}

	public void doFilter(ServletRequest request,
						 ServletResponse response,
						 FilterChain chain)
			throws IOException, ServletException {

		// There is actually no explicit "opening" of a Session, the
		// first call to HibernateUtil.beginTransaction() in control
		// logic (e.g. use case controller/event handler) will get
		// a fresh Session.
		try {
			chain.doFilter(request, response);

			// Commit any pending database transaction.
			HibernateUtil.commitTransaction();

		} finally {

			// No matter what happens, close the Session.
			HibernateUtil.closeSession();

		}
	}

	public void destroy() {}

}