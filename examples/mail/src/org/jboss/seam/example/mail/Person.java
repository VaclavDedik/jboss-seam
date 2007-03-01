package org.jboss.seam.example.mail;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.util.Resources;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Name("person")
public class Person implements Serializable
{
   private String firstname;
   private String lastname;
   private String address;
   private transient InputStream photo = Resources.getResourceAsStream("/no_image.png");
   
   @Factory("people")
   public List<Person> getPeople() {
      List<Person> people = new ArrayList<Person>();
      people.add(new Person("Gavin", "King", "gavin.king@jboss.com", "/gavin.jpg"));
      people.add(new Person("Shane", "Bryzak", "shane.bryzak@jboss.com", "/shane.jpg"));
      return people;
   }
   
   public Person()
   {
   }
   
   public Person(String firstname, String lastname, String address, String photoPath)
   {
      this.firstname = firstname;
      this.lastname = lastname;
      this.address = address;
      this.photo = Resources.getResourceAsStream(photoPath);
   }
   
   public Person(String firstname, String lastname, String address)
   {
      this.firstname = firstname;
      this.lastname = lastname;
      this.address = address;
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
      return photo;
   } 
}