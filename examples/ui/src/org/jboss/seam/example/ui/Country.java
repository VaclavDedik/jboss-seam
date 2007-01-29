package org.jboss.seam.example.ui;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Country
{
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   @Enumerated(EnumType.STRING)
   private Continent continent;

   public Continent getContinent()
   {
      return continent;
   }

   public void setContinent(Continent continent)
   {
      this.continent = continent;
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
   
   

}
