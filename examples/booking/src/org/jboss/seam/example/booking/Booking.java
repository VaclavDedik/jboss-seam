//$Id$
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.jboss.seam.annotations.Name;

@Entity
@Name("booking")
public class Booking implements Serializable
{
   private Long id;
   private User user;
   private Hotel hotel;
   private Date checkinDate;
   private Date checkoutDate;
   private String creditCard;
   
   public Booking() {}
   
   public Booking(Hotel hotel, User user)
   {
      this.hotel = hotel;
      this.user = user;
   }

   @Id @GeneratedValue
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
   
   @NotNull(message="Credit card number is required")
   @Length(min=16, max=16, message="Credit card number must 16 digits long")
   @Pattern(regex="\\d*", message="Credit card number must be numeric")
   public String getCreditCard()
   {
      return creditCard;
   }

   public void setCreditCard(String creditCard)
   {
      this.creditCard = creditCard;
   }
   
   @Transient
   public String getDescription()
   {
      DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
      return hotel.getName() + 
            ", " + df.format( getCheckinDate() ) + 
            " to " + df.format( getCheckoutDate() );
   }
   
   public String toString()
   {
      return "Booking(" + user + ","+ hotel + ")";
   }
}
