package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.core.Conversation;

@Scope(CONVERSATION)
@Name("bidAction")
public class BidAction
{   
   @In EntityManager entityManager;
   
   private Bid bid;
   
   @In(required = false)
   private Auction auction;
   
   @In(required = false)
   private Account authenticatedAccount;
   
   @Begin(join = true)
   public void placeBid()
   {
      bid = new Bid();
      bid.setAuction(auction);
      
      updateBid();
   }
   
   public void updateBid()
   {
      double amount = Double.parseDouble(Contexts.getEventContext().get("bidAmount").toString());
      
      if (amount >= bid.getAuction().getRequiredBid())
      {
         bid.setMaxAmount(amount);
      }      
   }
   
   @SuppressWarnings("unchecked")
   public String confirmBid()
   {
      // We set the user here because the user may not be authenticated when placeBid() is called. 
      bid.setAccount(authenticatedAccount);      
      bid.setBidDate(new Date());
      
      // This is where the tricky bidding logic happens
      
      entityManager.lock(bid.getAuction(), LockModeType.WRITE);
      entityManager.refresh(bid.getAuction());
      
      if (bid.getAuction().getStatus() != Auction.STATUS_LIVE)
      {
         return "ended";
      }
      else if (bid.getAuction().getEndDate().getTime() < bid.getBidDate().getTime())
      {
         bid.getAuction().setStatus(Auction.STATUS_COMPLETED);
         return "ended";
      }
      
      List<Bid> bids = entityManager.createQuery(
            "from Bid b where b.auction = :auction")
          .setParameter("auction", bid.getAuction())
          .getResultList();
      
      Bid highBid = null;
      
      for (Bid b : bids)
      {
         if (highBid == null)
         {
            highBid = b;
         }
         else if (b.getMaxAmount() > highBid.getMaxAmount())
         {
            highBid.setActualAmount(highBid.getMaxAmount());
            b.setActualAmount(Auction.getRequiredBid(highBid.getMaxAmount()));
            highBid = b;
         }
         else if (b.getMaxAmount() == highBid.getMaxAmount() &&
                  b.getBidDate().getTime() < highBid.getBidDate().getTime())
         {
            highBid.setActualAmount(highBid.getMaxAmount());
            b.setActualAmount(highBid.getMaxAmount());
            highBid = b;
         }
      }
      
      if (highBid == null)
      {
         // There are no bids so far...
         bid.setActualAmount(bid.getAuction().getRequiredBid());
         bid.getAuction().setHighBid(bid);
      }
      else if (bid.getMaxAmount() > highBid.getMaxAmount())
      {
         // If this bid is higher than the previous maximum bid, and is from
         // a different bidder, set the actual amount to the next required bid 
         // amount for the auction
         if (!bid.getAccount().equals(highBid.getAccount()))
         {
            bid.setActualAmount(Auction.getRequiredBid(highBid.getMaxAmount()));
         }        
         else
         {
            // Otherwise don't change the amount from the bidder's last bid
            bid.setActualAmount(highBid.getActualAmount());
         }
         bid.getAuction().setHighBid(bid);         
      }
      else
      {
         bid.setActualAmount(bid.getMaxAmount());
      }
      
      bid.getAuction().setBids(bid.getAuction().getBids() + 1);
      
      entityManager.persist(bid);      
      entityManager.flush();
      
      Conversation.instance().end();
      return "success";
   }
   
   public Bid getBid()
   {
      return bid;
   }   
   
   public boolean isValidBid()
   {
      return bid != null && bid.getMaxAmount() >= bid.getAuction().getRequiredBid();
   }
}
