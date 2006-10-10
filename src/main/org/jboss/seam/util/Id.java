//$Id$
package org.jboss.seam.util;

import java.util.concurrent.atomic.AtomicInteger;

public class Id
{
   private static AtomicInteger uniqueId = new AtomicInteger(0);
   
   public static String nextId() 
   {
      //TODO: this is not cluster safe!!!!!
      return Integer.toString( uniqueId.incrementAndGet() );
   }
}
