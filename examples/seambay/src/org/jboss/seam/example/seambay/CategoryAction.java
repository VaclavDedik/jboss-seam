package org.jboss.seam.example.seambay;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

@Name("categoryAction")
public class CategoryAction
{
   @In
   EntityManager entityManager;
   
   @Out(required = false)
   private List<Category> categories;
   
   @SuppressWarnings("unchecked")
   @Factory("categories")
   public void loadCategories()
   {
      categories = entityManager.createQuery("from Category order by name")
                   .getResultList();
   }
   
   public List<Category> getCategories()
   {
      return categories;
   }
}
