/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.http.HttpSession;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebSessionContext implements Context {

	private HttpSession session;
	
	public WebSessionContext(HttpSession session) {
		this.session = session;
	}

	public Object get(String name) {
		return session.getAttribute(name);
	}

	public void set(String name, Object value) {
		session.setAttribute(name, value);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		session.removeAttribute(name);
	}

	public String[] getNames() {
		Enumeration names = session.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
		while ( names.hasMoreElements() ) {
			results.add( (String) names.nextElement() );
		}
		return results.toArray(new String[]{});
	}
   
}
