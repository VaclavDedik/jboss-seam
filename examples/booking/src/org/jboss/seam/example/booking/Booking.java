//$Id$
package org.jboss.seam.example.booking;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratorType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TemporalType;

import org.hibernate.validator.NotNull;
import org.jboss.seam.annotations.Name;

@Entity
@Name("booking")
public class Booking
{
   private Long id;
   private User user;
   private Hotel hotel;
   private Date checkinDate;
   private Date checkoutDate;
   
   public Booking() {}
   
   public Booking(Hotel hotel, User user)
   {
      this.hotel = hotel;
      this.user = user;
   }

   @Id(generate=GeneratorType.AUTO)
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
   @NotNull
   @Basic(temporalType=TemporalType.DATE) 
   public Date getCheckinDate()
   {
      return checkinDate;
   }
   public void setCheckinDate(Date datetime)
   {
      this.checkinDate = datetime;
   }

   @ManyToOne @NotNull
   public Hotel getHotel()
   {
      return hotel;
   }
   public void setHotel(Hotel hotel)
   {
      this.hotel = hotel;
   }
   
   @ManyToOne @NotNull
   public User getUser()
   {
      return user;
   }
   public void setUser(User user)
   {
      this.user = user;
   }
   
   @Basic(temporalType=TemporalType.DATE) 
   @NotNull
   public Date getCheckoutDate()
   {
      return checkoutDate;
   }
   public void setCheckoutDate(Date checkoutDate)
   {
      this.checkoutDate = checkoutDate;
   }
   
   
}
