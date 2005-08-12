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
	public boolean isSet(String name);
}
