/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebRequestContext implements Context {

	private HttpServletRequest request;
	
	public WebRequestContext(HttpServletRequest request) {
		this.request = request;
	}

	public Object get(String name) {
		return request.getAttribute(name);
	}

	public void set(String name, Object value) {
		request.setAttribute(name, value);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

}
