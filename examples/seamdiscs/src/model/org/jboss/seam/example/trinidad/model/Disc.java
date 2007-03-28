package org.jboss.seam.example.trinidad.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Disc
{

   @Id @GeneratedValue
   private Integer id;
   
   private String name;
   
   private Date release;
   
   @ManyToOne
   private Artist artist;

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
   
   
   
}
