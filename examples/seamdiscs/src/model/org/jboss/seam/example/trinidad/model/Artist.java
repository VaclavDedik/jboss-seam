package org.jboss.seam.example.trinidad.model;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Artist
{
   
   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   @OneToMany(mappedBy="artist")
   private List<Disc> releases;

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

   public List<Disc> getReleases()
   {
      return releases;
   }

   public void setReleases(List<Disc> releases)
   {
      this.releases = releases;
   }
   
   
   
}
