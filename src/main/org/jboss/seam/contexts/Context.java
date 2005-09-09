/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.contexts;

import org.jboss.seam.ScopeType;

/**
 * A set of named components and items of data that
 * is associated with a particular seam context.
 * 
 * @author Gavin King
 * @version $Revision$
 */
public interface Context {
	public Object get(String name);
   public Object get(Class clazz);
	public void set(String name, Object value);
	public void remove(String name);
	public boolean isSet(String name);
	public String[] getNames();
   public void flush();
   public ScopeType getType();
}
