package org.jboss.seam.util;

import java.util.Enumeration;
import java.util.Iterator;

public class EnumerationIterator implements Iterator
{
   private Enumeration e;

   public EnumerationIterator(Enumeration e)
   {
      super();
      this.e = e;
   }

   public boolean hasNext()
   {
      return e.hasMoreElements();
   }

   public Object next()
   {
      return e.nextElement();
   }

   public void remove()
   {
      throw new UnsupportedOperationException();
   }
   
   
}
