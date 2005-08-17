/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.ServletContext;

/**
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class WebApplicationContext implements Context {

	private ServletContext context;
	
	public WebApplicationContext(ServletContext context) {
		this.context = context;
	}

	public Object get(String name) {
		return context.getAttribute(name);
	}

	public void set(String name, Object value) {
		context.setAttribute(name, value);
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		context.removeAttribute(name);
	}

	public String[] getNames() {
		Enumeration names = context.getAttributeNames();
		ArrayList<String> results = new ArrayList<String>();
		while ( names.hasMoreElements() ) {
			results.add( (String) names.nextElement() );
		}
		return results.toArray(new String[]{});
	}
   
}
