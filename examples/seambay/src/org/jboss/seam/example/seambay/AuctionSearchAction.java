package org.jboss.seam.example.seambay;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("auctionSearch")
@Scope(ScopeType.SESSION)
public class AuctionSearchAction
{
   @In
   EntityManager entityManager;
   
   private int pageSize = 10;
   private int page = 0;
   
   private String searchTerm;
   private Category searchCategory;
   
   private List<Auction> auctions;
   
   private Map<Category,Long> searchCategories = new HashMap<Category,Long>();

   @SuppressWarnings("unchecked")
   public void queryAuctions()
   {
      String qry = null;
      
      if (searchCategory == null)
      {
         qry = "from Auction a where lower(title) like #{searchPattern}"; 
      }
      else
      {
         qry = "from Auction a where lower(title) like #{searchPattern} and a.category = #{searchCategory}";
      }
      
      auctions = entityManager.createQuery(qry)
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();      
      
      searchCategories.clear();
      
      for (Object[] result : (List<Object[]>) entityManager.createQuery(
            "select a.category.categoryId, count(a) from Auction a " +
            "where lower(a.title) like #{searchPattern} " +
            "group by a.category.categoryId")
            .getResultList())
      {
         searchCategories.put(entityManager.find(Category.class, result[0]), (Long) result[1]);
      }
   }
   
   @Factory(value="searchPattern", scope=ScopeType.EVENT)
   public String getSearchPattern()
   {
      return searchTerm == null ? 
            "%" : '%' + searchTerm.toLowerCase().replace('*', '%') + '%';
   }   
 
   public String getSearchTerm()
   {
      return searchTerm;
   }
   
   public void setSearchTerm(String searchTerm)
   {
      this.searchTerm = searchTerm;
   }
   
   public int getPageSize()
   {
      return pageSize;
   }
   
   public void setPageSize(int pageSize)
   {
      this.pageSize = pageSize;
   }
   
   public int getPage()
   {
      return page;
   }
   
   public void setPage(int page)
   {
      this.page = page;
   }
   
   public List<Auction> getResults()
   {
      return auctions;
   }
   
   public List<Entry> getSearchCategories()
   {
      List<Entry> cats = new ArrayList<Entry>(searchCategories.entrySet());
      Collections.sort(cats, new Comparator<Entry>() {
        public int compare(Entry e1, Entry e2) {
           return ((Category) e1.getKey()).getName().compareToIgnoreCase(
                 ((Category) e2.getKey()).getName());
        }
      });
      return cats;
   }   
   
   public void selectCategory(Category category)
   {
      this.searchCategory = category;
      queryAuctions();
   }
   
   @Factory(value="searchCategory", scope=ScopeType.EVENT)
   public Category getSearchCategory()
   {
      return searchCategory;
   }
}
