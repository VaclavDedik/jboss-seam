//$Id$
package org.jboss.seam.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Around;
import org.jboss.seam.annotations.Within;
import org.jboss.seam.interceptors.AbstractInterceptor;

public class MergeSort<T>
{
   
   public List mergeSort(List<T> list) {
      Order order = new Order() {
         public boolean lessThan(Object a, Object b)
         {
            return ( (Comparable) a ).compareTo(b) < 0 ;
         }
      };
      return mergeSort(order, list);
   }
   
   public List mergeSort(final Comparator comparator, List<T> list) {
      Order order = new Order() {
         public boolean lessThan(Object a, Object b)
         {
            return comparator.compare(a, b) < 0 ;
         }
      };
      return mergeSort(order, list);
   }
   
   public List mergeSort(Order order, List<T> list)
   {
      return mergeSort(order, list, 0, list.size()-1);
   }

   private List mergeSort(Order order, List<T> objects, int startIndex, int endIndex) {
      if (startIndex > endIndex) 
      {
         return Collections.EMPTY_LIST;
      }
      else if (startIndex == endIndex) 
      {
         return Collections.singletonList( objects.get(startIndex) );
      }
      else {
         int midPoint = startIndex + (endIndex - startIndex)/2;
         return merge(
               order,
               mergeSort(order, objects, startIndex, midPoint),
               mergeSort(order, objects, midPoint + 1, endIndex)
            );
      }   
   }
   
   private List merge(Order order, List<T> listA, List<T> listB) {
      List<T> result = new ArrayList<T>();
      for (int i = 0, j = 0; (i < listA.size()) || (j < listB.size());) {
         if ((i < listA.size()) && ((j >= listB.size()) || (order.lessThan(listA.get(i), listB.get(j)))))
         {
            result.add(listA.get(i++));
         }
         else 
         {
            result.add(listB.get(j++));
         }
      }
      return result;
   }
   
   public interface Order {
      public boolean lessThan(Object a, Object b);
   }
   
   public static final void main(String[] args)
   {
      List list = new ArrayList();
      list.add("foo");
      list.add("bar");
      list.add("baz");
      list.add("fee");
      list.add("fi");
      list.add("xxx");
      list.add("lll");
      list.add("wqee");
      list.add("aaa");
      System.out.println( new MergeSort().mergeSort(list) );
      
      list.clear();
      list.add( new Foo() );
      list.add( new Bar() );
      list.add( new Baz() );
      list.add( new Fee() );
      System.out.println( new MergeSort().mergeSort(Component.INTERCEPTOR_ORDER, list) );
   }
   
   @Within(Baz.class) @Around(Bar.class)
   static class Foo extends AbstractInterceptor {}
   static class Bar extends AbstractInterceptor {}
   static class Baz extends AbstractInterceptor {}
   @Around(Bar.class)
   static class Fee extends AbstractInterceptor {}

}
