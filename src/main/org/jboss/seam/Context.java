/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

/**
 * @author Gavin King
 * @version $Revision$
 */
public interface Context {
	public Object get(String name);
	public void set(String name, Object value);
	public void remove(String name);
	public boolean isSet(String name);
	public String[] getNames();
   public void destroy();
}
