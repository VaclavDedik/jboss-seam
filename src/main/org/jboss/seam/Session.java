/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam;

import java.util.Enumeration;

/**
 * @author <a href="mailto:theute@jboss.org">Thomas Heute </a>
 * @version $Revision$
 */
public abstract class Session
{

   public abstract Object getAttribute(String key);

   public abstract void removeAttribute(String key);

   public abstract Enumeration getAttributeNames();

   public abstract void setAttribute(String key, Object value);

   public abstract void invalidate();

}


