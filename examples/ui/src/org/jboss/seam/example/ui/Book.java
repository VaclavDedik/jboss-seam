package org.jboss.seam.example.ui;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book implements Serializable
{
   
   @Id
   private BookPk bookPk = new BookPk();
   
   private String nationality;
   
   public String getName() 
   {
      return bookPk.getName();
   }
   
   public String getAuthor() 
   {
      return bookPk.getAuthor();
   }

   public String getNationality()
   {
      return nationality;
   }
   
}
