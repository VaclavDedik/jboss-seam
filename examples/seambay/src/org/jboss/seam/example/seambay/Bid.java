package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Bid implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private Integer bidId;
   private Auction auction;
   private User user;
   private Date bidDate;
   private double amount;
   
   @Id
   public Integer getBidId()
   {
      return bidId;
   }
   
   public void setBidId(Integer bidId)
   {
      this.bidId = bidId;
   }
   
   public Auction getAuction()
   {
      return auction;
   }
   
   public void setAuction(Auction auction)
   {
      this.auction = auction;
   }
   
   public User getUser()
   {
      return user;
   }
   
   public void setUser(User user)
   {
      this.user = user;
   }
   
   public Date getBidDate()
   {
      return bidDate;
   }
   
   public void setBidDate(Date bidDate)
   {
      this.bidDate = bidDate;
   }
   
   public double getAmount()
   {
      return amount;
   }
   
   public void setAmount(double amount)
   {
      this.amount = amount;
   }
}

