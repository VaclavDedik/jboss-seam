package org.jboss.seam.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sorter<T> 
{
   
	private void remove(SortItem<T> item, List<SortItem<T>> list)
   {
		list.remove(item);
		for (SortItem<T> o: list)
      {
			o.getWithinSortItems().remove(item);
		}
	}
   
	private SortItem<T> getInmost(List<SortItem<T>> list) 
   {
		SortItem<T> res=null;
		for (SortItem<T> o: list)
      {
			if ( o.getWithinSortItems().isEmpty() && nobodyWantsAround(o, list) )
         {
				res = o;
				break;
			}
		}
		return res;
	}
   
	private boolean nobodyWantsAround(SortItem<T> item, List<SortItem<T>> list)
   {
		boolean res = true;
		for (SortItem<T> o: list)
      {
			if ( o.getAroundSortItems().contains(item) )
         {
				res = false;
				break;
			}
		}
		return res;
	}
   
	public List<T> sort(List<SortItem<T>> sortable)
	{
      
	   // Build a map of items
      Map<Object, SortItem<T>> sortItemsMap = new HashMap<Object, SortItem<T>>();
      for (SortItem<T> item : sortable)
      {
         if (item.isAddable())
         {         
             sortItemsMap.put(item.getKey(), item);
         }
      }
      
      List<SortItem<T>> sortItemsList = new ArrayList<SortItem<T>>();
      
      for ( Object key : sortItemsMap.keySet() )
      {
         SortItem<T> sortItem = sortItemsMap.get(key);
         for ( Object aroundKey : sortItem.getAround() )
         {
            if ( sortItemsMap.get( aroundKey ) != null )
            {
               sortItem.getAroundSortItems().add( sortItemsMap.get(aroundKey) );
            }
         }
         for ( Object withinKey : sortItem.getWithin() )
         {
            if ( sortItemsMap.get( withinKey ) != null )
            {
               sortItem.getWithinSortItems().add( sortItemsMap.get( withinKey ) );
            }
         }
         sortItemsList.add(sortItem);
      }

      List<SortItem<T>> result = new ArrayList<SortItem<T>>();
      SortItem<T> inmost = null;
      
      do 
      {  
         inmost = getInmost(sortItemsList);
         if (inmost!=null)
         {
            result.add(inmost);
            remove(inmost, sortItemsList);
         }
      }
      while ( !sortItemsList.isEmpty() && inmost!=null );
      
      if ( !sortItemsList.isEmpty() )
      {
         throw new IllegalArgumentException("Can not sort list:" + sortItemsList);
      }
      
      
      List<T> sorted = new ArrayList<T>();
      for (SortItem<T> sortItem : result)
      {
         sorted.add( sortItem.getObject() );
      }
      return sorted;
	}
	
	
	
}
