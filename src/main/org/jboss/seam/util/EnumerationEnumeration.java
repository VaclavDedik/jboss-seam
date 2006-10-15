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
      return more() || currentHasMoreElements();
   }

   public T nextElement()
   {
      while ( more() )
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

   private boolean more()
   {
      return loc<enumerations.length;
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
