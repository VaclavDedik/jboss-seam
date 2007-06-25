package org.jboss.seam.exception;

import org.jboss.seam.faces.Navigator;

/**
 * An element of the chain that knows how to handle a 
 * specific exception type.
 * 
 * @author Gavin King
 *
 */
public abstract class ExceptionHandler extends Navigator
{
   public abstract void handle(Exception e) throws Exception;
   public abstract boolean isHandler(Exception e);
}