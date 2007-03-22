package org.jboss.seam.example.seambay;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Category implements Serializable
{
   private static final long serialVersionUID = 1L;
   
   private Integer categoryId;
   private String name;
   
   @Id
   public Integer getCategoryId()
   {
      return categoryId;
   }
   
   public void setCategoryId(Integer categoryId)
   {
      this.categoryId = categoryId;
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
