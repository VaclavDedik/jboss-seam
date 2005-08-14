/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.util.ArrayList;
import java.util.Enumeration;

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

	public void clear(String name) {
		request.removeAttribute(name);
	}

	public void destroy() {
		throw new UnsupportedOperationException();
	}

	public String[] getNames() {
		Enumeration names = request.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
		while ( names.hasMoreElements() ) {
			results.add( (String) names.nextElement() );
		}
		return results.toArray(new String[]{});
	}
}
