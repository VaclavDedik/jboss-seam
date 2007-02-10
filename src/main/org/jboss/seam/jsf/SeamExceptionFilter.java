/*
 * JBoss, Home of Professional Open Source
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.jboss.seam.jsf;


/**
 * As a last line of defence, rollback uncommitted transactions 
 * at the very end of the request.
 * 
 * @deprecated use org.jboss.seam.servlet.SeamExceptionFilter
 * @author Gavin King
 */
public class SeamExceptionFilter extends org.jboss.seam.web.ExceptionFilter {}
