package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

@Entity
public class Auction implements Serializable
{
   private static final long serialVersionUID = -8349473227099432431L;

   private Integer auctionId;
   private User user;
   private Category category;
   private String title;
   private String description;
   private Date endDate;
   private AuctionImage image;
   private int bids;
   private double price;
   
   @Id @GeneratedValue
   public Integer getAuctionId()
   {
      return auctionId;
   }
   
   public void setAuctionId(Integer auctionId)
   {
      this.auctionId = auctionId;
   }
   
   @ManyToOne
   @JoinColumn(name = "USER_ID")
   public User getUser()
   {
      return user;
   }
   
   public void setUser(User user)
   {
      this.user = user;
   }
   
   @ManyToOne
   @JoinColumn(name = "CATEGORY_ID")
   public Category getCategory()
   {
      return category;
   }
   
   public void setCategory(Category category)
   {
      this.category = category;
   }
   
   public String getTitle()
   {
      return title;
   }
   
   public void setTitle(String title)
   {
      this.title = title;
   }
   
   public String getDescription()
   {
      return description;
   }
   
   public void setDescription(String description)
   {
      this.description = description;
   }
   
   public Date getEndDate()
   {
      return endDate;
   }
   
   public void setEndDate(Date endDate)
   {
      this.endDate = endDate;
   }
   
   @OneToOne
   @JoinColumn(name = "IMAGE_ID")
   public AuctionImage getImage()
   {
      return image;
   }
   
   public void setImage(AuctionImage image)
   {
      this.image = image;
   }
   
   public int getBids()
   {
      return bids;
   }
   
   public void setBids(int bids)
   {
      this.bids = bids;
   }
   
   @Transient
   public String getTimeLeft()
   {
      return (endDate.getTime() - System.currentTimeMillis()) + "ms"; 
   }
   
   public double getPrice()
   {
      return price;
   }
   
   public void setPrice(double price)
   {
      this.price = price;
   }
}
