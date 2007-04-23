package org.jboss.seam.example.seambay;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("auctionDetail")
public class AuctionDetailAction
{
   @In EntityManager entityManager;
   
   private int selectedAuctionId;
   
   private Auction auction;
   
   @Factory("auction")
   public Auction getAuction()
   {
      auction = entityManager.find(Auction.class, selectedAuctionId);
      return auction;
   }
   
   public int getSelectedAuctionId()
   {
      return selectedAuctionId;
   }
   
   public void setSelectedAuctionId(int selectedAuctionId)
   {
      this.selectedAuctionId = selectedAuctionId;
   }
}
