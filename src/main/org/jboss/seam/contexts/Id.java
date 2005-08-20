//$Id$
package org.jboss.seam.contexts;

import java.util.concurrent.atomic.AtomicInteger;

public class Id
{
   private static AtomicInteger uniqueId = new AtomicInteger(0);
   
   public static String nextId() 
   {
      return Integer.toString( uniqueId.incrementAndGet() );
   }
}
