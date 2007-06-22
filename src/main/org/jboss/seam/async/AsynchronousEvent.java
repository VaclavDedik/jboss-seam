package org.jboss.seam.async;

import org.jboss.seam.core.Events;

/**
 * An asynchronous event
 * 
 * @author Gavin King
 *
 */
public class AsynchronousEvent extends Asynchronous
{
   static final long serialVersionUID = 2074586442931427819L;
   
   private String type;
   private Object[] parameters;

   public AsynchronousEvent(String type, Object[] parameters)
   {
      this.type = type;
      this.parameters = parameters;
   }

   @Override
   public void call()
   {
      Events.instance().raiseEvent(type, parameters);
   }
   
}