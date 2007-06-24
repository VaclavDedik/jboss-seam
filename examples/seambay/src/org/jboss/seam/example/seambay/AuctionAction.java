package org.jboss.seam.example.seambay;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.security.Restrict;

/**
 * This component is used to create new auctions, and is invoked via both the
 * web interface and the AuctionService web service. 
 *  
 * @author Shane Bryzak
 */
@Transactional
@Scope(CONVERSATION)
@Name("auctionAction")
@Restrict("#{identity.loggedIn}")
public class AuctionAction implements Serializable
{
   private static final long serialVersionUID = -6738397725125671313L;
   
   private static final int DEFAULT_AUCTION_DURATION = 7;
   
   @In EntityManager entityManager;
   
   @In Account authenticatedAccount;

   @Out
   private Auction auction;
   
   private int durationDays;
   
   @Begin
   @SuppressWarnings("unchecked")
   public void createAuction()
   {
      if (auction == null)
      {
         auction = new Auction();
         auction.setAccount(authenticatedAccount);
         auction.setStatus(Auction.STATUS_UNLISTED);   
      }
      
      durationDays = DEFAULT_AUCTION_DURATION;
   }   
   
   public void setDetails(String title, String description, int categoryId)
   {
      auction.setTitle(title);
      auction.setDescription(description);
      auction.setCategory(entityManager.find(Category.class, categoryId));      
   }
   
   /**
    * Allows the auction duration to be overidden from the default
    * 
    * @param days Number of days to set the auction duration to.
    */
   public void setDuration(int days)
   {
      this.durationDays = days;
   }
   
   @End
   public void confirm()
   {
      Calendar cal = new GregorianCalendar(); 
      cal.add(Calendar.DAY_OF_MONTH, durationDays);
      auction.setEndDate(cal.getTime());
      auction.setStatus(Auction.STATUS_LIVE);
      entityManager.persist(auction);
   }

   public Auction getAuction()
   {
      return auction;
   }
}
