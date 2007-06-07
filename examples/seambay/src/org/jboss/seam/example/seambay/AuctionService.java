package org.jboss.seam.example.seambay;

import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.jboss.seam.Component;
import org.jboss.seam.annotations.Conversation;
import org.jboss.seam.annotations.ConversationId;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Identity;

@Stateless
@WebService
@Name("auctionService")
@Conversation("createAuction")
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
      
      List<Category> categories = catAction.getAllCategories();
      
      return categories.toArray(new Category[categories.size()]);
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
   public Auction getNewAuctionDetails(@ConversationId int auctionId)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      // TODO remove the auction image from the result
      
      return action.getAuction();
   }
   
   @WebMethod
   public void updateAuction(@ConversationId int auctionId, String title, 
         String description, int categoryId)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      action.editAuction(auctionId);
      action.setDetails(title, description, categoryId);
   }
   
   @WebMethod
   public void setAuctionDuration(@ConversationId int auctionId, int days)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      action.setDuration(days);
   }
   
   @WebMethod
   public void confirmAuction(@ConversationId int auctionId)
   {
      AuctionAction action = (AuctionAction) Component.getInstance(AuctionAction.class, true);
      
      action.confirm();
   }
   
   @WebMethod
   public Auction[] findAuctions(String searchTerm)
   {
      AuctionSearchAction search = (AuctionSearchAction) Component.getInstance(
            AuctionSearchAction.class, true);
 
      search.setSearchTerm(searchTerm);
      search.queryAuctions();
            
      return search.getAuctions().toArray(new Auction[search.getAuctions().size()]);
   }   
}
