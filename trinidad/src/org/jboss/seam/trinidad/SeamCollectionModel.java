package org.jboss.seam.trinidad;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.myfaces.trinidad.model.CollectionModel;
import org.apache.myfaces.trinidad.model.SortCriterion;
import org.jboss.seam.framework.Query;

/**
 * Abstract base class for an Apache Trinidad CollectionModel
 * 
 * Implementing classes need to provide conversion between the
 * current row index and a key for the unchanging row.
 * 
 * Using rowIndex when backing the CollectionModel with a Query
 * is not possible as sorting and paging alters the rowIndex
 * outside the control of the CollectionModel.
 * 
 * @author pmuir
 *
 */
public abstract class SeamCollectionModel extends CollectionModel
{

   //private Object rowKey;
   
   private int rowIndex = -1;
   
   private List<SortCriterion> criteria;
   
   @Override
   public Object getWrappedData()
   {
      return getWrappedList();
   }

   public void refresh()
   {
      getQuery().refresh();
   }

   @Override
   public int getRowCount()
   {
      return getQuery().getResultCount().intValue();
   }

   @Override
   public void setWrappedData(Object arg0)
   {
      throw new UnsupportedOperationException("Immutable DataModel");
   }

   protected List getWrappedList()
   {
      return getQuery().getResultList();
   }
   
   protected abstract Query getQuery();
   
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
         criteria = asCriteria(getQuery().getOrder());
      }
      return criteria;
   }

   @Override
   public void setSortCriteria(List<SortCriterion> criteria)
   {
      if (criteria != null && !criteria.equals(this.criteria))
      {
         getQuery().setOrder(asQl(criteria));
         this.criteria = null;
         refresh();
      }
   }
   
   @Override
   public void setRowIndex(int rowIndex)
   {
      this.rowIndex = rowIndex;
      //rowKey = null;
   }

   @Override
   public int getRowIndex()
   {
      return rowIndex;
   }

   @Override
   public Object getRowData()
   {
      // We can attempt to do lazy loading
      if (getQuery().getMaxResults() != null)
      {
         boolean refresh = false;
         // Lazy load data
         refresh = page();
         if (refresh)
         {
            refresh();
         }
         return getWrappedList().get(getRowIndex() - getFirstResult());
      }
      else
      {
         return getWrappedList().get(getRowIndex());
      }
   }

   private boolean page()
   {
      if (getRowIndex() < getFirstResult())
      {
         while (getRowIndex() < getFirstResult())
         {
            getQuery().previous();
         }
         return true;
      }
      else if (getRowIndex() >= getQuery().getNextFirstResult())
      {
         while (getRowIndex() >= getQuery().getNextFirstResult())
         {
            getQuery().next();
         }
         return true;
      }
      else
      {
         return false;
      }
   }

   protected int getFirstResult()
   {
      if (getQuery().getFirstResult() == null)
      {
         getQuery().setFirstResult(0);
      }
      return getQuery().getFirstResult();
   }

   @Override
   public boolean isRowAvailable()
   {
      return getRowIndex() >= 0 && getRowIndex() < getRowCount();
   }
   
   protected String asQl(List<SortCriterion> criteria)
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

   protected List<SortCriterion> asCriteria(String sql)
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

}