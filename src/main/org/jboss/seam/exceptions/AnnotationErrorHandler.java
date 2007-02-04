/**
 * 
 */
package org.jboss.seam.exceptions;

import org.jboss.seam.annotations.HttpError;

public class AnnotationErrorHandler extends ErrorHandler
{
   @Override
   public boolean isHandler(Exception e)
   {
      return e.getClass().isAnnotationPresent(HttpError.class);
   }
   
   @Override
   protected String getMessage(Exception e)
   {
      return e.getClass().getAnnotation(HttpError.class).message();
   }
   
   @Override
   protected int getCode(Exception e)
   {
      return e.getClass().getAnnotation(HttpError.class).errorCode();
   }
   
   @Override
   protected boolean isEnd(Exception e)
   {
      return e.getClass().getAnnotation(HttpError.class).end();
   }
   
}