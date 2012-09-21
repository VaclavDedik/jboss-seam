//$Id: Hotel.java 5579 2007-06-27 00:06:49Z gavin $
package org.jboss.seam.example.booking;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jboss.seam.annotations.Name;

import org.metawidget.inspector.annotation.*;
import org.metawidget.inspector.faces.*;

@Entity
@Name("hotel")
public class Hotel implements Serializable
{
   private Long id;
   private String name;
   private String address;
   private String city;
   private String state;
   private String zip;
   private String country;
   private BigDecimal price;
   
   @Id @GeneratedValue
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
   
   @Size(max=50) @NotNull
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   
   @Size(max=100) @NotNull
   @UiComesAfter("name")
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }

   @Size(max=40) @NotNull
   @UiComesAfter("address")
   public String getCity()
   {
      return city;
   }
   public void setCity(String city)
   {
      this.city = city;
   }

   @Size(min=4, max=6) @NotNull
   @UiComesAfter("state")
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }

   @Size(min=2, max=10) @NotNull
   @UiComesAfter("city")
   public String getState()
   {
      return state;
   }
   public void setState(String state)
   {
      this.state = state;
   }

   @Size(min=2, max=40) @NotNull
   @UiComesAfter("zip")
   public String getCountry()
   {
      return country;
   }
   public void setCountry(String country)
   {
      this.country = country;
   }

   @Column(precision=6, scale=2)
   @UiComesAfter("country")
   @UiFacesNumberConverter(type="currency",currencySymbol="$")
   @UiLabel("Nightly rate")
   public BigDecimal getPrice()
   {
      return price;
   }
   public void setPrice(BigDecimal price)
   {
      this.price = price;
   }

   @Override
   public String toString()
   {
      return "Hotel(" + name + "," + address + "," + city + "," + zip + ")";
   }
}
