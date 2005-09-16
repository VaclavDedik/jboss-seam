//$Id$
package org.jboss.seam.example.noejb;

import static javax.persistence.GeneratorType.AUTO;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.hibernate.validator.Length;
import org.hibernate.validator.NotNull;
import org.hibernate.validator.Pattern;
import org.jboss.seam.annotations.Name;

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
   
   @Id(generate=AUTO)
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
   
   @Length(max=50) @NotNull
   public String getName()
   {
      return name;
   }
   public void setName(String name)
   {
      this.name = name;
   }
   
   @Length(max=100) @NotNull
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   
   @Length(max=20) @NotNull
   public String getCity()
   {
      return city;
   }
   public void setCity(String city)
   {
      this.city = city;
   }
   
   @Length(min=4, max=5) @NotNull
   @Pattern(regex="\\d{4,5}")
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   
   @Length(min=2, max=2) @NotNull
   public String getState()
   {
      return state;
   }
   public void setState(String state)
   {
      this.state = state;
   }

   public String toString()
   {
      return "Hotel(" + address + "," + city + "," + zip + ")";
   }
}
