package org.jboss.seam.example.seambay;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.seam.Component;
import org.jboss.seam.security.Identity;

@Stateless
@WebService
public class AuctionService implements AuctionServiceRemote
{      
   @WebMethod
   public boolean login(String username, String password)
   {
      Identity.instance().setUsername(username);
      Identity.instance().setPassword(password);
      Identity.instance().login();
      return Identity.instance().isLoggedIn();
   }
   
   @WebMethod
   public boolean logout()
   {
      Identity.instance().logout();
      return !Identity.instance().isLoggedIn();
   }
   
   @WebMethod
   public Category[] listCategories()
   {
      CategoryAction catAction = (CategoryAction) Component.getInstance(
            CategoryAction.class, true);
      
      catAction.loadCategories();
      
      return catAction.getCategories().toArray(new Category[catAction.getCategories().size()]);
   }
   
   @WebMethod
   public Integer createAuction(String title, String description, int categoryId)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      action.createAuction();
      action.setDetails(title, description, categoryId);
      
      return action.getAuction().getAuctionId();
   }
   
   @WebMethod
   public void updateAuction(int auctionId, String title, String description, int categoryId)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      action.editAuction(auctionId);
      action.setDetails(title, description, categoryId);
   }
   
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
}
