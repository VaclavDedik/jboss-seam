package org.jboss.seam.trinidad;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.myfaces.trinidad.model.SortCriterion;
import org.jboss.seam.framework.Query;

public class TrinidadCollectionModel extends org.apache.myfaces.trinidad.model.CollectionModel
{
   
   private int rowIndex;

   private Query query;
   
   private List<SortCriterion> criteria;
   
   @Override
   public Object getWrappedData()
   {
      return query.getResultList();
   }

   public TrinidadCollectionModel(Query query)
   {
      this.query = query;
   }

   public void refresh()
   {
      query.refresh();
   }

   @Override
   public boolean isSortable(String property)
   {
      return true;
   }

   @Override
   public List<SortCriterion> getSortCriteria()
   {
      if (criteria == null)
      {
         criteria = asCriteria(query.getOrder());
      }
      return criteria;
   }

   @Override
   public void setSortCriteria(List<SortCriterion> criteria)
   {
      if (criteria != null && !criteria.equals(this.criteria))
      {
         query.setOrder(asSql(criteria));
         this.criteria = null;
         refresh();
      }
   }
   
   private String asSql(List<SortCriterion> criteria)
   {
      if (criteria != null && criteria.size() > 0)
      {
         String sql = "";
         for (SortCriterion sortCriterion : criteria)
         {
            sql += sortCriterion.getProperty() + (sortCriterion.isAscending() ? " ASC" : " DESC");
         }
         if (!"".equals(sql))
         {
            sql.substring(0, sql.length() - 1);
         }
         return sql;
      }
      else
      {
         return null;
      }

   }

   private List<SortCriterion> asCriteria(String sql)
   {
      List<SortCriterion> criteria = new ArrayList<SortCriterion>();
      if (!(sql == null || "".equals(sql)))
      {
         StringTokenizer tokenizer = new StringTokenizer(sql, ",");
         while (tokenizer.hasMoreTokens())
         {
            SortCriterion sortCriterion;
            String fragment = tokenizer.nextToken();
            String s = fragment.substring(fragment.lastIndexOf(" "));
            if (" ASC".equalsIgnoreCase(s))
            {
               sortCriterion = new SortCriterion(fragment.substring(0, fragment.length() - 4), true);
            }
            else if (" DESC".equalsIgnoreCase(s))
            {
               sortCriterion = new SortCriterion(fragment.substring(0, fragment.length() - 5),
                        false);
            }
            else
            {
               sortCriterion = new SortCriterion(fragment, false);
            }
            criteria.add(sortCriterion);
         }
      }
      return criteria;
   }

   @Override
   public int getRowCount()
   {
      return query.getResultCount().intValue();
   }

   @Override
   public void setWrappedData(Object arg0)
   {
      throw new UnsupportedOperationException("Immutable");
   }
   
   private List getWrappedList()
   {
      return (List) getWrappedData();
   }
   
   @Override
   public void setRowIndex(int rowIndex)
   {
     this.rowIndex = rowIndex;
   }

   @Override
   public int getRowIndex()
   {
      return rowIndex;
   }

   /**
    * Gets the row key of the current row
    * @inheritDoc
    */
   @Override
   public Object getRowKey()
   {
     return isRowAvailable()
       ? String.valueOf(getRowIndex())
       : null;
   }

   /**
    * Finds the row with the matching key and makes it current
    * @inheritDoc
    */
   @Override
   public void setRowKey(Object key)
   {
     setRowIndex(_toRowIndex((String) key));
   }
   
   /**
    * Gets the row key of the current row
    * @inheritDoc
    */
   /*@Override
   public Object getRowKey()
   {
      if (rowIndex == -1)
      {
         return null;
      }
      else
      {
         return EntityIdentifierStore.instance().put(getWrappedList().get(getRowIndex()));
      }
   }*/

   /**
    * Finds the row with the matching key and makes it current
    * @inheritDoc
    */
   /*@Override
   public void setRowKey(Object key)
   {
     if (key == null)
     {
        setRowIndex(-1);
     }
     else
     {
        Object entity = EntityIdentifierStore.instance().get((Integer) key);
        setRowIndex(getWrappedList().indexOf(entity));
     }
   }*/
   
   private int _toRowIndex(String rowKey)
   {
     if (rowKey == null)
       return -1;

     try
     {
       return Integer.parseInt(rowKey);
     }
     catch (NumberFormatException nfe)
     {
       return -1;
     }
   }

   @Override
   public Object getRowData()
   {
      return getWrappedList().get(rowIndex);
   }

   @Override
   public boolean isRowAvailable()
   {
      return rowIndex >= 0 && rowIndex < getWrappedList().size();
   }

   
}
