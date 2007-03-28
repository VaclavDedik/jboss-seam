package org.jboss.seam.example.trinidad;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.apache.myfaces.trinidad.model.SortableModel;
import org.jboss.seam.framework.EntityQuery;


public class TrinidadEntityQuery extends EntityQuery
{
   private List<SortCriterion> sortCriteria;
   
   @Override
   public void setOrder(String order)
   {
      sortCriteria = asCriteria(order);
   }
   
   @Override
   public String getOrder()
   {
      return asSql(sortCriteria);
   }
   
   private String asSql (List<SortCriterion> criteria)
   {
      String sql = "";
      for (SortCriterion sortCriterion : criteria)
      {
         sql += sortCriterion.getProperty() + (sortCriterion.isAscending() ? " ASC" : " DESC");
      }
      if (!"".equals(sql))
      {
         sql.substring( 0, sql.length() -1 );
      }
      return sql;
   }
   
   private List<SortCriterion> asCriteria(String sql)
   {
      List<SortCriterion>  criteria = new ArrayList<SortCriterion>();
      StringTokenizer tokenizer = new StringTokenizer(sql, ",");
      while (tokenizer.hasMoreTokens())
      {
         SortCriterion sortCriterion;
         String fragment = tokenizer.nextToken();
         String s = fragment.substring(fragment.lastIndexOf(" "));
         if ("ASC".equalsIgnoreCase(s))
         {
            sortCriterion = new SortCriterion(fragment.substring(0, fragment.length() - 3), true);
         }
         else if ("DESC".equalsIgnoreCase(s))
         {
            sortCriterion = new SortCriterion(fragment.substring(0, fragment.length() - 4), false);
         }
         else
         {
            sortCriterion = new SortCriterion(fragment, false);
         }
         criteria.add(sortCriterion);
      }
      return criteria;
   }
   
   public CollectionModel getCollectionModel()
   {
      return new SortableModel() 
      {             
         @Override
         public boolean isSortable(String property)
         {
            return true;
         }
         
         @Override
         public List<SortCriterion> getSortCriteria()
         {
            return sortCriteria;
         }
         
         @Override
         public void setSortCriteria(List<SortCriterion> criteria)
         {
            sortCriteria = criteria;
         }
         
      };
   }
}
