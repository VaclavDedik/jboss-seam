package org.jboss.seam.example.seamdiscs.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.Length;

@Entity
public class Disc
{

   @Id @GeneratedValue
   private Integer id;
   
   @Length(max=2, message="#{messages.descriptionError}")
   private String name;
   
   private Date release;
   
   @ManyToOne(cascade={CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
   private Artist artist;
   
   @Length(max=2, message="#{messages.descriptionError}")
   private String description;
   
   public Disc()
   {
   }

   public Disc(Artist artist)
   {
      this.artist = artist;
   }

   public Artist getArtist()
   {
      return artist;
   }

   public void setArtist(Artist artist)
   {
      this.artist = artist;
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

   public Date getRelease()
   {
      return release;
   }

   public void setRelease(Date release)
   {
      this.release = release;
   }
   
   public String getDescription()
   {
      return description;
   }
   
   public void setDescription(String description)
   {
      this.description = description;
   }
   
}
