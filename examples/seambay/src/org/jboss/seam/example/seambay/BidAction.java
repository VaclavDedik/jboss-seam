package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.util.Date;

import javax.persistence.EntityManager;

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
   private User authenticatedUser;
   
   @Begin(join = true)
   public void placeBid()
   {
      bid = new Bid();
      bid.setAmount(Double.parseDouble(Contexts.getEventContext().get("bidAmount").toString()));
      bid.setAuction(auction);
   }
   
   public String confirmBid()
   {
      bid.setUser(authenticatedUser);
      bid.setBidDate(new Date());
      
      entityManager.persist(bid);
      
      Conversation.instance().end();
      return "success";
   }
   
   public Bid getBid()
   {
      return bid;
   }   
}
