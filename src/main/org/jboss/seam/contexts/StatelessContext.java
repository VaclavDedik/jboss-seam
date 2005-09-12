/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.Seam;

/**
 * For stateless objects
 * 
 * @author Gavin King
 * @author <a href="mailto:theute@jboss.org">Thomas Heute</a>
 * @version $Revision$
 */
public class StatelessContext implements Context {

   public ScopeType getType()
   {
      return ScopeType.STATELESS;
   }

	public Object get(String name) {
		try {
			return new InitialContext().lookup(name);
		}
		catch (NamingException ne) {
			return null;
		}
	}

	public void set(String name, Object value) {
		try {
			new InitialContext().bind(name, value);
		}
		catch (NamingException ne) {
			throw new IllegalArgumentException("could not bind: " + name, ne);
		}
	}

	public boolean isSet(String name) {
		return get(name)!=null;
	}

	public void remove(String name) {
		try {
			new InitialContext().unbind(name);
		}
		catch (NamingException ne) {
			throw new IllegalArgumentException("could not unbind: " + name, ne);
		}
	}

	public String[] getNames() {
		throw new UnsupportedOperationException();
	}

   public Object get(Class clazz)
   {
      return get( Seam.getComponentName(clazz) );
   }

   public void flush() {}
}
