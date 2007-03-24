package org.jboss.seam.example.seambay;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Auction implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private Integer auctionId;
   private User user;
   private Category category;
   private String title;
   private String description;
   private Date endDate;
   
   @Id
   public Integer getAuctionId()
   {
      return auctionId;
   }
   
   public void setAuctionId(Integer auctionId)
   {
      this.auctionId = auctionId;
   }
   
   @ManyToOne
   public User getUser()
   {
      return user;
   }
   
   public void setUser(User user)
   {
      this.user = user;
   }
   
   @ManyToOne
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
}
