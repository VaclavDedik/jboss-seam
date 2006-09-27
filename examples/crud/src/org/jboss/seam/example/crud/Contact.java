package org.jboss.seam.example.crud;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Contact
{
   @Id @GeneratedValue 
   private Long id;
   
   private String firstName;
   private String lastName;
   private String address;
   private String city;
   private String state;
   private String zip;
   private String country;
   private String homePhone;
   private String businessPhone;
   private String cellPhone;
   
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   public String getBusinessPhone()
   {
      return businessPhone;
   }
   public void setBusinessPhone(String businessPhone)
   {
      this.businessPhone = businessPhone;
   }
   public String getCellPhone()
   {
      return cellPhone;
   }
   public void setCellPhone(String cellPhone)
   {
      this.cellPhone = cellPhone;
   }
   public String getCity()
   {
      return city;
   }
   public void setCity(String city)
   {
      this.city = city;
   }
   public String getCountry()
   {
      return country;
   }
   public void setCountry(String country)
   {
      this.country = country;
   }
   public String getFirstName()
   {
      return firstName;
   }
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }
   public String getHomePhone()
   {
      return homePhone;
   }
   public void setHomePhone(String homePhone)
   {
      this.homePhone = homePhone;
   }
   public String getLastName()
   {
      return lastName;
   }
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }
   public String getState()
   {
      return state;
   }
   public void setState(String state)
   {
      this.state = state;
   }
   public String getZip()
   {
      return zip;
   }
   public void setZip(String zip)
   {
      this.zip = zip;
   }
   public Long getId()
   {
      return id;
   }
   public void setId(Long id)
   {
      this.id = id;
   }
}
