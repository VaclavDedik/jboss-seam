package org.jboss.seam.util;

import java.util.ArrayList;
import java.util.List;


public abstract class SortItem<T>
{
   
   private List<SortItem> around = new ArrayList<SortItem>();
   private List<SortItem> within = new ArrayList<SortItem>();

   final List<SortItem> getAroundSortItems()
   {
      return around;
   }
   
   final List<SortItem> getWithinSortItems()
   {
      return within;
   }

   public Object getKey()
   {
      return getObject().hashCode();
   }
   
   protected abstract T getObject();
   
   public boolean isAddable()
   {
      return true;
   }
   
   public abstract List<? extends Object> getAround();
   public abstract List<? extends Object> getWithin();
   
   @Override
   public String toString()
   {
      return getObject().toString() + " within [" + getWithin() + "] around [" + getAround() + "]";
   }
   
}
