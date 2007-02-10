/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.servlet;

import org.jboss.seam.web.ExceptionFilter;

/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @author Gavin King
 */
@Deprecated
public class SeamExceptionFilter extends ExceptionFilter
{
}
