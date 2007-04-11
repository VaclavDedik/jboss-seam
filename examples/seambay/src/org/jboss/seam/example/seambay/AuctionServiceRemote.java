package org.jboss.seam.example.seambay;

import javax.ejb.Remote;

@Remote
public interface AuctionServiceRemote
{
   boolean login(String username, String password);
   boolean logout();
   
   Category[] listCategories();
   
   Integer createAuction(String title, String description, int categoryId);   
   void updateAuction(int auctionId, String title, String description, int categoryId);
   
   Auction[] findAuctions(String searchTerm);

}
