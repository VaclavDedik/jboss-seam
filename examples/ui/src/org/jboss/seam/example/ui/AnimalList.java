package org.jboss.seam.example.ui;

import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Unwrap;

@Name("animals")
public class AnimalList
{

   private List<String> animals;
   
   @Unwrap
   public List<String> unwrap()
   {
      if (animals == null)
      {
         animals = new ArrayList<String>();
         animals.add("Dog");
         animals.add("Cat");
         animals.add("Goldfish");
         animals.add("Rabbit");
         animals.add("Snake");
         animals.add("Parrot");
      }
      return animals;
   }
   
   
}
