package org.hibernate.ce.auction.persistence;

import org.hibernate.Session;
import org.apache.commons.logging.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * A servlet filter that disconnects and reconnects a Hibernate Session for each request.
 * <p>
 * Use this filter for the <b>session-per-application-transaction</b> pattern
 * with a <i>Long Session</i>. Don't forget to demarcate application transactions
 * in your code, as described in Hibernate in Action.
 *
 * @see HibernateUtil
 * @author Christian Bauer <christian@hibernate.org>
 */
public class HibernateFilterLong
		implements Filter {

	private static final String HTTPSESSIONKEY = "HibernateSession";
	private static Log log = LogFactory.getLog(HibernateFilterLong.class);

	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Servlet filter init, now disconnecting/reconnecting a Session for each request.");
	}

	public void doFilter(ServletRequest request,
						 ServletResponse response,
						 FilterChain chain)
			throws IOException, ServletException {

		// Try to get a Hibernate Session from the HttpSession
		HttpSession userSession =
				((HttpServletRequest) request).getSession();
		Session hibernateSession =
				(Session) userSession.getAttribute(HTTPSESSIONKEY);

		if (hibernateSession != null)
			HibernateUtil.reconnect(hibernateSession);

		// If there is no Session, the first call to
		// HibernateUtil.beginTransaction in application code will open
		// a new Session for this thread.
		try {
			chain.doFilter(request, response);

			// Commit any pending database transaction.
			HibernateUtil.commitTransaction();

		} finally {
			// TODO: The Session should be closed if a fatal exceptions occurs

			// No matter what happens, disconnect the Session.
			hibernateSession = HibernateUtil.disconnectSession();
			// and store it in the users HttpSession
			userSession.setAttribute(HTTPSESSIONKEY, hibernateSession);
		}
	}

	public void destroy() {}

}