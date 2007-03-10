package org.jboss.seam.example.ui;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book
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
