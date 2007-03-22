package org.jboss.seam.example.ui;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Person implements Serializable
{
   
   public enum Honorific {
      
      MR("Mr."), 
      MRS("Mrs."), 
      MISS("Miss."), 
      MS("Ms."),
      DOCTOR("Dr.");
      
      private String label;
      
      Honorific(String label)
      {
         this.label = label;
      }
      
      public String getLabel()
      {
         return label;
      }
      
   }
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   // A wikitext string
   private String hobbies;
   
   @ManyToOne
   private Country country;
   
   @ManyToOne
   private Continent continent;
   
   @Enumerated(EnumType.STRING)
   private Honorific honorific;
   
   private int age;
   
   @ManyToMany
   private List<Colour> favouriteColours;
   
   @ManyToOne
   private Book favouriteBook;

   public Country getCountry()
   {
      return country;
   }

   public void setCountry(Country country)
   {
      this.country = country;
   }

   public Integer getId()
   {
      return id;
   }

   public void setId(Integer id)
   {
      this.id = id;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public int getAge()
   {
      return age;
   }
   
   public void setAge(int age)
   {
      this.age = age;
   }
   
   public Continent getContinent()
   {
      return continent;
   }
   
   public void setContinent(Continent continent)
   {
      this.continent = continent;
   }
   
   public Honorific getHonorific()
   {
      return honorific;
   }
   
   public void setHonorific(Honorific honorific)
   {
      this.honorific = honorific;
   }
   
   public List<Colour> getFavouriteColours()
   {
      return favouriteColours;
   }
   
   public void setFavouriteColours(List<Colour> favouriteColours)
   {
      this.favouriteColours = favouriteColours;
   }
   
   public Book getFavouriteBook()
   {
      return favouriteBook;
   }
   
   public void setFavouriteBook(Book favouriteBook)
   {
      this.favouriteBook = favouriteBook;
   }
   
   public String getHobbies()
   {
      return hobbies;
   }
   
   public void setHobbies(String hobbies)
   {
      this.hobbies = hobbies;
   }
}
