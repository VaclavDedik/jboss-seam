//$Id$
package org.jboss.seam.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Uses a brute force O(n^4) algorithm to discover an acceptable
 * order for partially ordered elements where the transitivity
 * of the ordering is not known in advance. Of course, this
 * should only be used to order small numbers of elements.
 * 
 * @author Gavin King
 */
public abstract class Sorter<T>
{
   public void sort(List<T> originalList)
   {
      List<T> remaining = new ArrayList(originalList);
      originalList.clear();
      sortInto(originalList, remaining);
      
      if ( !isGoodOrder(originalList) )
      {
         throw new IllegalStateException();
      }
   }
   
   private boolean sortInto(List<T> result, List<T> remaining)
   {
      if (remaining.size()==0)
      {
         return true;
      }
      else 
      {
         int loc = result.size();
         result.add(null);
         for (int i=0; i<remaining.size(); i++)
         {
            List<T> nowRemaining = new ArrayList<T>(remaining);
            T interceptor = nowRemaining.remove(i);
            result.set(loc, interceptor);
            if ( isGoodOrder(result) )
            {
               if ( sortInto(result, nowRemaining) )
               {
                  return true;
               }
            }
         }
         result.remove(loc);
         return false;
      }
   }
   
   private boolean isGoodOrder(List<T> list)
   {
      for (int i = 0; i < list.size(); i++)
      {
         for (int j=0; j<i; j++)
         {
            if ( isOrderViolated( list.get(j), list.get(i) ) ) return false;
         }
      }
      return true;
   }

   protected abstract boolean isOrderViolated(T outside, T inside);
   
}
