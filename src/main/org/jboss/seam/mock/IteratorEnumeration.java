//$Id$
package org.jboss.seam.mock;

import java.util.Enumeration;
import java.util.Iterator;

public class IteratorEnumeration implements Enumeration
{
   
   private Iterator iterator;
   
   IteratorEnumeration(Iterator iterator)
   {
      this.iterator = iterator;
   }

   public boolean hasMoreElements()
   {
      return iterator.hasNext();
   }

   public Object nextElement()
   {
      return iterator.next();
   }

}
