package org.jboss.seam.example.seambay;

import javax.ejb.Stateless;
import javax.jws.HandlerChain;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.seam.Component;

@Stateless
@WebService
@HandlerChain(file="META-INF/handler-chain.xml")
public class AuctionService implements AuctionServiceRemote
{   
   @WebMethod
   public Auction[] findAuctions(String searchTerm)
   {
      AuctionSearchAction search = (AuctionSearchAction) Component.getInstance(
            AuctionSearchAction.class, true);
 
      search.setSearchTerm(searchTerm);
      search.queryAuctions();
      
      // TODO - trim the result somehow, or use DTOs.  We don't want to send user records
      // (including their passwords!!) here, nor do we want to send a huge object graph.
      
      return search.getResults().toArray(new Auction[search.getResults().size()]);
   }
   
   @WebMethod
   public Auction getAuctionDetails(Integer auctionId)
   {
      return null;
   }
   
   @WebMethod
   public Category[] listCategories()
   {
      CategoryAction catAction = (CategoryAction) Component.getInstance(
            CategoryAction.class, true);
      
      catAction.loadCategories();
      
      return catAction.getCategories().toArray(new Category[catAction.getCategories().size()]);
   }
}
