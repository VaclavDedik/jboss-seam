package org.jboss.seam.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EnumerationEnumeration<T> implements Enumeration<T>
{
   private Enumeration<T>[] enumerations;
   private int loc = 0;

   public EnumerationEnumeration(Enumeration<T>[] enumerations)
   {
      this.enumerations = enumerations;
   }

   public boolean hasMoreElements()
   {
      return loc < enumerations.length-1 || currentHasMoreElements();
   }

   public T nextElement()
   {
      while ( loc < enumerations.length )
      {
         if ( currentHasMoreElements() )
         {
            return currentNextElement();
         }
         else
         {
            loc++;
         }
      }
      throw new NoSuchElementException();
   }

   private T currentNextElement()
   {
      return enumerations[loc].nextElement();
   }

   private boolean currentHasMoreElements()
   {
      return enumerations[loc].hasMoreElements();
   }
    
}
