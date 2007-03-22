package org.jboss.seam.example.seambay;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.seam.Component;

@Stateless
@WebService
public class AuctionService implements AuctionServiceRemote
{   
   @WebMethod
   public Auction[] findAuctions(String searchTerm)
   {
      AuctionSearchAction search = (AuctionSearchAction) Component.getInstance(
            AuctionSearchAction.class, true);
 
      search.setSearchTerm(searchTerm);
      search.queryAuctions();
      
      return search.getResults().toArray(new Auction[search.getResults().size()]);
   }
}
