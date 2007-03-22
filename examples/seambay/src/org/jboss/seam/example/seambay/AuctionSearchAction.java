package org.jboss.seam.example.seambay;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.datamodel.DataModel;

@Name("auctionSearch")
@Scope(ScopeType.SESSION)
public class AuctionSearchAction
{
   @In
   EntityManager entityManager;
   
   private int pageSize = 10;
   private int page = 0;
   
   private String searchTerm;
   
   @DataModel
   private List<Auction> auctions;

   public void queryAuctions()
   {
      auctions = entityManager.createQuery(
            "from Auction a where lower(title) like #{pattern}")
            .setMaxResults(pageSize)
            .setFirstResult( page * pageSize )
            .getResultList();      
   }
   
   @Factory(value="pattern", scope=ScopeType.EVENT)
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
}
