package org.jboss.seam.example.seambay;

import javax.ejb.Remote;

@Remote
public interface AuctionServiceRemote
{
   boolean login(String username, String password);
   boolean logout();
   
   Category[] listCategories();
   
   Integer createAuction(String title, String description, int categoryId);   
   Auction getNewAuctionDetails(int auctionId);
   void updateAuction(int auctionId, String title, String description, int categoryId);
   void setAuctionDuration(int auctionId, int days);
   void confirmAuction(int auctionId);
   
   Auction[] findAuctions(String searchTerm);
}
