/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.core.Navigator;

public abstract class ExceptionHandler extends Navigator
{
   public abstract void handle(Exception e) throws Exception;
   public abstract boolean isHandler(Exception e);
}