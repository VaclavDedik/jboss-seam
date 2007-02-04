package org.jboss.seam.example.mail;

import java.io.InputStream;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Resources;

@Name("person")
@Scope(ScopeType.CONVERSATION)
public class Person
{
   private String firstname;
   private String lastname;
   private String address;
   
   @Create
   @Begin(join=true)
   public void create() 
   {
      
   }
   
   public String getAddress()
   {
      return address;
   }
   public void setAddress(String address)
   {
      this.address = address;
   }
   public String getFirstname()
   {
      return firstname;
   }
   public void setFirstname(String firstname)
   {
      this.firstname = firstname;
   }
   public String getLastname()
   {
      return lastname;
   }
   public void setLastname(String lastname)
   {
      this.lastname = lastname;
   }
   
   public InputStream getPhoto() {
      return Resources.getResourceAsStream("/no_image.png");
   }
   
}
